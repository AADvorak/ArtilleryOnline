import type {Position} from "~/playground/data/common";
import {Circle, HalfCircle, type Segment} from "~/playground/data/geometry";
import {VectorUtils} from "~/playground/utils/vector-utils";
import {BattleUtils} from "~/playground/utils/battle-utils";

export const GeometryUtils = {

  findClosestPosition(position: Position, positions: Position[]): Position {
    let closest = positions[0]!
    let closestDistance = BattleUtils.distance(position, closest)
    for (let i = 1; i < positions.length; i++) {
      const point = positions[i]!
      const distance = BattleUtils.distance(position, point)
      if (distance < closestDistance) {
        closest = point
        closestDistance = distance
      }
    }
    return closest
  },

  getPointToSegmentProjection(
      point: Position,
      segment: Segment
  ): Position | null {
    return this.getPointToSegmentOrLineProjection(point, segment, false)
  },

  getPointToLineProjection(
      point: Position,
      segment: Segment
  ): Position {
    return this.getPointToSegmentOrLineProjection(point, segment, true)!
  },

  getPointToSegmentOrLineProjection(
      point: Position,
      segment: Segment,
      notCheckInside: boolean
  ): Position | null {
    const A = point.x - segment.begin.x
    const B = point.y - segment.begin.y
    const C = segment.end.x - segment.begin.x
    const D = segment.end.y - segment.begin.y

    const dot = A * C + B * D
    const squareLength = C * C + D * D
    const param = squareLength !== 0 ? dot / squareLength : -1

    const projection: Position = {
      x: segment.begin.x + param * C,
      y: segment.begin.y + param * D
    }

    if (notCheckInside) {
      return projection
    }

    if (param >= 0 && param <= 1) {
      return projection
    }

    return null
  },

  isPointLyingOnArc(pointAngle: number, beginAngle: number, endAngle: number): boolean {
    const shiftedPointAngle = pointAngle + 2 * Math.PI
    return (pointAngle > beginAngle && pointAngle < endAngle) ||
        (shiftedPointAngle > beginAngle && shiftedPointAngle < endAngle)
  },

  calculateAngleDiff(objectAngle: number, targetAngle: number): number {
    let diff = targetAngle - objectAngle
    if (Math.abs(diff) > Math.PI) {
      if (diff > 0) {
        return 2 * Math.PI - diff
      } else {
        return 2 * Math.PI + diff
      }
    } else {
      return diff
    }
  },

  getSegmentsIntersectionPoint(s1: Segment, s2: Segment): Position | null {
    const x1 = s1.begin.x
    const y1 = s1.begin.y
    const x2 = s1.end.x
    const y2 = s1.end.y

    const x3 = s2.begin.x
    const y3 = s2.begin.y
    const x4 = s2.end.x
    const y4 = s2.end.y

    const denominator = (x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4)

    if (Math.abs(denominator) < 1e-9) {
      return null
    }

    const t = ((x1 - x3) * (y3 - y4) - (y1 - y3) * (x3 - x4)) / denominator
    const u = -((x1 - x2) * (y1 - y3) - (y1 - y2) * (x1 - x3)) / denominator

    if (t > 0 && t < 1 && u > 0 && u < 1) {
      const x = x1 + t * (x2 - x1)
      const y = y1 + t * (y2 - y1)
      return {x, y}
    } else {
      return null
    }
  },

  getSegmentAndHalfCircleIntersectionPoints(segment: Segment, halfCircle: HalfCircle): Set<Position> {
    const circlePoints = this.getSegmentAndCircleIntersectionPoints(segment, halfCircle.circle())
    const intersectionPoints = new Set<Position>()

    for (const point of circlePoints) {
      const pointAngle = VectorUtils.angleFromTo(halfCircle.center, point)
      if (this.isPointLyingOnArc(pointAngle, halfCircle.angle, halfCircle.angle + Math.PI)) {
        intersectionPoints.add(point)
      }
    }

    return intersectionPoints
  },

  getSegmentAndCircleIntersectionPoints(segment: Segment, circle: Circle): Set<Position> {
    if (Math.abs(segment.begin.x - segment.end.x) > Math.abs(segment.begin.y - segment.end.y)) {
      return this.getSegmentAndCircleIntersectionPointsByX(segment, circle)
    }
    return this.getSegmentAndCircleIntersectionPointsByY(segment, circle)
  },

  getCirclesIntersectionPoints(circle1: Circle, circle2: Circle): Set<Position> {
    const center1 = circle1.center
    const center2 = circle2.center
    const radius1 = circle1.radius
    const radius2 = circle2.radius
    const diffX = center2.x - center1.x
    const diffY = center2.y - center1.y
    const c = -0.5 * (Math.pow(radius2, 2) - Math.pow(radius1, 2) - Math.pow(diffX, 2) - Math.pow(diffY, 2))
    const a = Math.pow(diffX, 2) + Math.pow(diffY, 2)

    if (Math.abs(diffX) > Math.abs(diffY)) {
      const b = -2.0 * diffY * c
      const e = Math.pow(c, 2) - Math.pow(radius1, 2) * Math.pow(diffX, 2)
      const d = Math.pow(b, 2) - 4 * a * e

      if (d < 0) {
        return new Set()
      }
      if (d === 0) {
        const y = -b / (2 * a)
        const x = (c - y * diffY) / diffX
        const position = this.createPosition(x + center1.x, y + center1.y)
        return new Set([position])
      }

      const y1 = (-b + Math.sqrt(d)) / (2 * a)
      const y2 = (-b - Math.sqrt(d)) / (2 * a)
      const x1 = (c - y1 * diffY) / diffX
      const x2 = (c - y2 * diffY) / diffX

      const position1 = this.createPosition(x1 + center1.x, y1 + center1.y)
      const position2 = this.createPosition(x2 + center1.x, y2 + center1.y)
      return new Set([position1, position2])
    } else {
      const b = -2.0 * diffX * c
      const e = Math.pow(c, 2) - Math.pow(radius1, 2) * Math.pow(diffY, 2)
      const d = Math.pow(b, 2) - 4 * a * e

      if (d < 0) {
        return new Set()
      }
      if (d === 0) {
        const x = -b / (2 * a)
        const y = (c - x * diffX) / diffY
        const position = this.createPosition(x + center1.x, y + center1.y)
        return new Set([position])
      }

      const x1 = (-b + Math.sqrt(d)) / (2 * a)
      const x2 = (-b - Math.sqrt(d)) / (2 * a)
      const y1 = (c - x1 * diffX) / diffY
      const y2 = (c - x2 * diffX) / diffY

      const position1 = this.createPosition(x1 + center1.x, y1 + center1.y)
      const position2 = this.createPosition(x2 + center1.x, y2 + center1.y)
      return new Set([position1, position2])
    }
  },

  createPosition(x: number, y: number): Position {
    return {
      x,
      y,
    }
  },

  getSegmentAndCircleIntersectionPointsByX(segment: Segment, circle: Circle): Set<Position> {
    const lineA = (segment.begin.y - segment.end.y) / (segment.begin.x - segment.end.x)
    const lineB = segment.end.y - lineA * segment.end.x
    const circleA = 1 + Math.pow(lineA, 2)
    const circleB = 2 * lineA * (lineB - circle.center.y) - 2 * circle.center.x
    const circleC = Math.pow(circle.center.x, 2) - Math.pow(circle.radius, 2) +
        Math.pow(lineB - circle.center.y, 2)
    const discriminant = Math.pow(circleB, 2) - 4 * circleA * circleC

    if (discriminant < 0) {
      return new Set()
    }

    const xMin = Math.min(segment.begin.x, segment.end.x)
    const xMax = Math.max(segment.begin.x, segment.end.x)

    let xSet: Set<number>
    if (discriminant === 0) {
      xSet = new Set([-circleB / (2 * circleA)])
    } else {
      xSet = new Set([
        (-circleB + Math.sqrt(discriminant)) / (2 * circleA),
        (-circleB - Math.sqrt(discriminant)) / (2 * circleA)
      ])
    }

    const intersectionPoints = new Set<Position>()
    for (const x of xSet) {
      if (x <= xMax && x >= xMin) {
        const position = this.createPosition(x, lineA * x + lineB)
        intersectionPoints.add(position)
      }
    }

    return intersectionPoints
  },

  getSegmentAndCircleIntersectionPointsByY(segment: Segment, circle: Circle): Set<Position> {
    const lineA = (segment.begin.x - segment.end.x) / (segment.begin.y - segment.end.y)
    const lineB = segment.end.x - lineA * segment.end.y
    const circleA = 1 + Math.pow(lineA, 2)
    const circleB = 2 * lineA * (lineB - circle.center.x) - 2 * circle.center.y
    const circleC = Math.pow(circle.center.y, 2) - Math.pow(circle.radius, 2) +
        Math.pow(lineB - circle.center.x, 2)
    const discriminant = Math.pow(circleB, 2) - 4 * circleA * circleC

    if (discriminant < 0) {
      return new Set()
    }

    const yMin = Math.min(segment.begin.y, segment.end.y)
    const yMax = Math.max(segment.begin.y, segment.end.y)

    let ySet: Set<number>
    if (discriminant === 0) {
      ySet = new Set([-circleB / (2 * circleA)])
    } else {
      ySet = new Set([
        (-circleB + Math.sqrt(discriminant)) / (2 * circleA),
        (-circleB - Math.sqrt(discriminant)) / (2 * circleA)
      ])
    }

    const intersectionPoints = new Set<Position>()
    for (const y of ySet) {
      if (y <= yMax && y >= yMin) {
        const position = this.createPosition(lineA * y + lineB, y)
        intersectionPoints.add(position)
      }
    }

    return intersectionPoints
  },
}
