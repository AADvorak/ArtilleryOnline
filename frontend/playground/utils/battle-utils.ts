import type { RoomSpecs } from '@/playground/data/specs'
import type { RoomModel } from '@/playground/data/model'
import type {BodyPosition, Position, Shift} from "@/playground/data/common";
import type {BodyParticleState, ParticleState} from "~/playground/data/state";
import {Segment} from "~/playground/data/geometry";
import {GeometryUtils} from "~/playground/utils/geometry-utils";

export const BattleUtils = {
  getRightWall(roomSpecs: RoomSpecs): Segment {
    const rightBottom = {
      x: roomSpecs.rightTop.x,
      y: roomSpecs.leftBottom.y,
    }
    return new Segment(rightBottom, roomSpecs.rightTop)
  },

  getLeftWall(roomSpecs: RoomSpecs): Segment {
    const leftTop = {
      x: roomSpecs.leftBottom.x,
      y: roomSpecs.rightTop.y,
    }
    return new Segment(leftTop, roomSpecs.leftBottom)
  },

  getRoomWidth(roomSpecs: RoomSpecs) {
    return roomSpecs.rightTop.x - roomSpecs.leftBottom.x
  },

  getRoomHeight(roomSpecs: RoomSpecs) {
    return roomSpecs.rightTop.y - roomSpecs.leftBottom.y
  },

  getGroundPointsNumber(roomSpecs: RoomSpecs) {
    return Math.floor(this.getRoomWidth(roomSpecs) / roomSpecs.step)
  },

  getGroundSegmentsBetween(xMin: number, xMax: number, roomModel: RoomModel): Segment[] {
    const positions: Position[] = this.getGroundIndexesBetween(xMin, xMax, roomModel)
        .map(index => this.getGroundPosition(index, roomModel))
    if (positions.length < 2) {
      return []
    }
    const segments: Segment[] = []
    for (let j = 0; j < positions.length - 1; j++) {
      segments.push(new Segment(positions[j]!, positions[j + 1]!))
    }
    return segments
  },

  getGroundIndexesBetween(xMin: number, xMax: number, roomModel: RoomModel): number[] {
    const roomWidth = this.getRoomWidth(roomModel.specs)
    const groundPointsNumber = this.getGroundPointsNumber(roomModel.specs)
    let minGroundIndex = Math.floor((groundPointsNumber * xMin) / roomWidth)
    let maxGroundIndex = Math.ceil((groundPointsNumber * xMax) / roomWidth)

    if (minGroundIndex < 0) {
      minGroundIndex = 0
    }
    if (maxGroundIndex >= groundPointsNumber) {
      maxGroundIndex = groundPointsNumber - 1
    }

    const groundIndexes = []
    for (let i = minGroundIndex; i < maxGroundIndex; i++) {
      groundIndexes.push(i)
    }

    return groundIndexes
  },

  getNearestGroundPosition(x: number, roomModel: RoomModel) {
    const roomWidth = this.getRoomWidth(roomModel.specs)
    const groundPointsNumber = this.getGroundPointsNumber(roomModel.specs)
    const objectPositionIndex = (groundPointsNumber * x) / roomWidth
    let nearestGroundIndex = Math.floor(objectPositionIndex)

    if (nearestGroundIndex < 0) {
      nearestGroundIndex = 0
    }
    if (nearestGroundIndex >= groundPointsNumber) {
      nearestGroundIndex = groundPointsNumber - 1
    }

    return this.getGroundPosition(nearestGroundIndex, roomModel)
  },

  getGroundPosition(index: number, roomModel: RoomModel): Position {
    const groundLine = roomModel.state.groundLine
    const x = index * roomModel.specs.step
    if (groundLine) {
      return {x, y: groundLine[index]!}
    } else {
      return {x, y: roomModel.specs.leftBottom.y}
    }
  },

  distance(p1: Position, p2: Position) {
    return Math.sqrt(Math.pow(p1.x - p2.x, 2.0) + Math.pow(p1.y - p2.y, 2.0));
  },

  shiftedPosition(position: Position, distance: number, angle: number): Position {
    return {
      x: position.x + distance * Math.cos(angle),
      y: position.y + distance * Math.sin(angle)
    }
  },

  shiftedBodyPosition(position: BodyPosition, distance: number, angle: number): BodyPosition {
    return {
      x: position.x + distance * Math.cos(angle),
      y: position.y + distance * Math.sin(angle),
      angle: position.angle
    }
  },

  shiftPosition(position: Position, shift: Shift) {
    position.x += shift.distance * Math.cos(shift.angle)
    position.y += shift.distance * Math.sin(shift.angle)
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

  generateParticle(position: Position, lifeTime?: number, angle?: number, magnitude?: number): ParticleState {
    const velocityMagnitude = magnitude || 2 + 0.5 * Math.random()
    const velocityAngle = angle || Math.PI / 4 + Math.PI * Math.random() / 2
    const remainTime = lifeTime || 0.3 * Math.random()
    const {x, y} = position
    return {
      position: {x, y},
      velocity: {
        x: velocityMagnitude * Math.cos(velocityAngle),
        y: velocityMagnitude * Math.sin(velocityAngle),
      },
      remainTime
    }
  },

  generateBodyParticle(position: BodyPosition, lifeTime?: number, moveAngle?: number,
                       magnitude?: number, angleVelocity?: number): BodyParticleState {
    const velocityMagnitude = magnitude || 2 + 0.5 * Math.random()
    const velocityAngle = moveAngle || Math.PI / 4 + Math.PI * Math.random() / 2
    const remainTime = lifeTime || 0.3 * Math.random()
    const {x, y, angle} = position
    return {
      position: {x, y, angle},
      velocity: {
        x: velocityMagnitude * Math.cos(velocityAngle),
        y: velocityMagnitude * Math.sin(velocityAngle),
        angle: angleVelocity || 0
      },
      remainTime
    }
  },

  /**
   * This method does not work same way as corresponding backend method
   */
  getFirstPointUnderGround(segment: Segment, roomModel: RoomModel): Position | null {
    const xMin = Math.min(segment.begin.x, segment.end.x)
    const xMax = Math.max(segment.begin.x, segment.end.x)
    const indexes = this.getGroundIndexesBetween(xMin, xMax, roomModel)
    
    if (indexes.length === 0) {
      const beginNearestPosition = this.getNearestGroundPosition(segment.begin.x, roomModel)
      const endNearestPosition = this.getNearestGroundPosition(segment.end.x, roomModel)
      if (beginNearestPosition.y > segment.begin.y) {
        return beginNearestPosition
      }
      if (endNearestPosition.y > segment.end.y) {
        return endNearestPosition
      }
      return null
    }
    
    const start = segment.begin.x < segment.end.x ? 0 : indexes.length - 1
    const increment = segment.begin.x < segment.end.x ? 1 : -1
    
    for (let index = start; index >= 0 && index < indexes.length; index += increment) {
      const groundPosition = this.getGroundPosition(indexes[index]!, roomModel)
      const segmentPosition = segment.findPointWithX(groundPosition.x)
      if (segmentPosition && groundPosition.y > segmentPosition.y) {
        // difference with backend
        return groundPosition
      }
    }
    
    return null
  },

  getGroundSegmentAround(x: number, roomModel: RoomModel): Segment | null {
    const roomWidth = this.getRoomWidth(roomModel.specs)
    const groundPointsNumber = this.getGroundPointsNumber(roomModel.specs)
    const minGroundIndex = Math.floor((groundPointsNumber * x) / roomWidth)
    const maxGroundIndex = Math.ceil((groundPointsNumber * x) / roomWidth)
    if (minGroundIndex >= 0 && maxGroundIndex < groundPointsNumber) {
      return new Segment(
        this.getGroundPosition(minGroundIndex, roomModel),
        this.getGroundPosition(maxGroundIndex, roomModel)
      )
    }
    return null
  },

  /**
   * This method does not work same way as corresponding backend method
   */
  getGroundIntersectionPoint(segment: Segment, roomModel: RoomModel): Position | null {
    const xMin = Math.min(segment.begin.x, segment.end.x)
    const xMax = Math.max(segment.begin.x, segment.end.x)
    const indexes = this.getGroundIndexesBetween(xMin, xMax, roomModel)
    if (!indexes.length) {
      const groundSegment = this.getGroundSegmentAround(xMin, roomModel)
      if (groundSegment) {
        return GeometryUtils.getSegmentsIntersectionPoint(segment, groundSegment)
      }
    }
    const start = segment.begin.x < segment.end.x ? 0 : indexes.length - 1
    const increment = segment.begin.x < segment.end.x ? 1 : -1
    for (let index = start; index >= 0 && index < indexes.length; index += increment) {
      const groundPosition = this.getGroundPosition(indexes[index]!, roomModel)
      const segmentPosition = segment.findPointWithX(groundPosition.x)
      if (segmentPosition && groundPosition.y > segmentPosition.y) {
        const previousGroundPosition = this.getGroundPosition(indexes[index]! - increment, roomModel)
        if (previousGroundPosition) {
          const groundSegment = new Segment(previousGroundPosition, groundPosition)
          const intersectionPoint = GeometryUtils.getSegmentsIntersectionPoint(segment, groundSegment)
          if (intersectionPoint) {
            return intersectionPoint
          }
        }
        // difference with backend
        return groundPosition
      }
    }
    return null
  }
}
