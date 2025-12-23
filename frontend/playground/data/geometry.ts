import type {BodyPosition, Position, Vector, Velocity} from "~/playground/data/common";
import {
  type CircleShape,
  type HalfCircleShape,
  type Shape,
  ShapeNames,
  type TrapezeShape
} from "~/playground/data/shapes";
import {BattleUtils} from "~/playground/utils/battle-utils";
import {VectorUtils} from "~/playground/utils/vector-utils";
import type {SurfaceState} from "~/playground/data/state";

export class Boundaries {
  constructor(
      readonly xMin: number,
      readonly xMax: number,
      readonly yMin: number,
      readonly yMax: number
  ) {}

  noOverlap(boundaries: Boundaries): boolean {
    return this.xMax < boundaries.xMin || this.xMin > boundaries.xMax
        || this.yMax < boundaries.yMin || this.yMin > boundaries.yMax
  }
}

export interface BodyPart {
  getPosition: () => BodyPosition
  getShape: () => Shape
  getBoundaries: () => Boundaries
}

export class Segment {
  begin: Position
  end: Position

  constructor(begin: Position, end: Position) {
    this.begin = begin
    this.end = end
  }

  findPointWithX(targetX: number): Position | null {
    const x1 = this.begin.x
    const y1 = this.begin.y
    const x2 = this.end.x
    const y2 = this.end.y

    if ((targetX < x1 && targetX < x2) || (targetX > x1 && targetX > x2)) {
      return null
    }

    const y = y1 + ((targetX - x1) * (y2 - y1)) / (x2 - x1)
    return { x: targetX, y }
  }

  center(): Position {
    return {
      x: (this.begin.x + this.end.x) / 2,
      y: (this.begin.y + this.end.y) / 2,
    }
  }

  normal(): Vector {
    return VectorUtils.normal(VectorUtils.angleFromTo(this.end, this.begin))
  }
}

export class Circle implements BodyPart {
  center: Position
  radius: number

  constructor(center: Position, radius: number) {
    this.center = center
    this.radius = radius
  }

  getPosition(): BodyPosition {
    return {
      ...this.center,
      angle: 0
    }
  }

  getShape(): CircleShape {
    return {
      name: ShapeNames.CIRCLE,
      radius: this.radius
    }
  }

  getBoundaries(): Boundaries {
    return new Boundaries(
        this.center.x - this.radius,
        this.center.x + this.radius,
        this.center.y - this.radius,
        this.center.y + this.radius
    )
  }

  static of(position: BodyPosition, shape: CircleShape): Circle {
    return new Circle(position, shape.radius)
  }
}

export class HalfCircle implements BodyPart {
  center: Position
  radius: number
  angle: number

  constructor(center: Position, radius: number, angle: number) {
    this.center = center
    this.radius = radius
    this.angle = angle
  }

  getPosition(): BodyPosition {
    return {
      ...this.center,
      angle: this.angle
    }
  }

  getShape(): HalfCircleShape {
    return {
      name: ShapeNames.HALF_CIRCLE,
      radius: this.radius
    }
  }

  getBoundaries(): Boundaries {
    return new Boundaries(
        this.center.x - this.radius,
        this.center.x + this.radius,
        this.center.y - this.radius,
        this.center.y + this.radius
    )
  }

  chord(): Segment {
    return new Segment(this.bottomLeft(), this.bottomRight())
  }

  bottomRight(): Position {
    return BattleUtils.shiftedPosition(this.center, this.radius, this.angle)
  }

  bottomLeft(): Position {
    return BattleUtils.shiftedPosition(this.center, -this.radius, this.angle)
  }

  circle(): Circle {
    return new Circle(this.center, this.radius)
  }

  static of(bodyPosition: BodyPosition, radius: number): HalfCircle {
    return new HalfCircle({x: bodyPosition.x, y: bodyPosition.y}, radius, bodyPosition.angle)
  }
}

export class Trapeze implements BodyPart {
  position: BodyPosition
  shape: TrapezeShape

  constructor(position: BodyPosition, shape: TrapezeShape) {
    this.position = position
    this.shape = shape
  }

  getPosition(): BodyPosition {
    return this.position
  }

  getShape(): TrapezeShape {
    return this.shape
  }

  getBoundaries(): Boundaries {
    const maxDistance = this.maxDistanceFromCenter()
    return new Boundaries(
        this.position.x - maxDistance,
        this.position.x + maxDistance,
        this.position.y - maxDistance,
        this.position.y + maxDistance
    )
  }

  bottomRight(): Position {
    return BattleUtils.shiftedPosition(this.position, this.shape.bottomRadius, this.position.angle)
  }

  bottomLeft(): Position {
    return BattleUtils.shiftedPosition(this.position, -this.shape.bottomRadius, this.position.angle)
  }

  topRight(): Position {
    return BattleUtils.shiftedPosition(this.topCenter(), this.shape.topRadius, this.position.angle)
  }

  topLeft(): Position {
    return BattleUtils.shiftedPosition(this.topCenter(), -this.shape.topRadius, this.position.angle)
  }

  bottom(): Segment {
    return new Segment(this.bottomLeft(), this.bottomRight())
  }

  top(): Segment {
    return new Segment(this.topRight(), this.topLeft())
  }

  right(): Segment {
    return new Segment(this.bottomRight(), this.topRight())
  }

  left(): Segment {
    return new Segment(this.topLeft(), this.bottomLeft())
  }

  maxDistanceFromCenter(): number {
    const topCornerDistance = Math.sqrt(
        this.shape.topRadius ** 2 + this.shape.height ** 2
    )
    return Math.max(topCornerDistance, this.shape.bottomRadius)
  }

  private topCenter(): Position {
    return BattleUtils.shiftedPosition(this.position, this.shape.height, this.position.angle + Math.PI / 2)
  }

  static ofSurface(surface: SurfaceState): Trapeze {
    const segment = new Segment(surface.begin, surface.end)
    const geometryPosition: BodyPosition = {
      ...VectorUtils.shifted(segment.center(), VectorUtils.multiply(segment.normal(), -surface.width / 2)),
      angle: VectorUtils.angleFromTo(surface.begin, surface.end)
    }
    const length = BattleUtils.distance(segment.begin, segment.end)
    return new Trapeze(geometryPosition, {
      name: ShapeNames.TRAPEZE,
      bottomRadius: length / 2,
      topRadius: length / 2,
      height: surface.width
    })
  }
}

export class VectorProjections {
  readonly angle: number
  normal: number
  tangential: number

  constructor(
      angle: number,
      normal: number,
      tangential: number
  ) {
    this.angle = angle
    this.normal = normal
    this.tangential = tangential
  }

  recoverVelocity(): Velocity {
    return {
      x: this.getX(),
      y: this.getY(),
    }
  }

  recoverPosition(): Position {
    return {
      x: this.getX(),
      y: this.getY(),
    }
  }

  static of(vector: Vector, angle: number): VectorProjections {
    const normal = vector.x * Math.sin(angle) - vector.y * Math.cos(angle)
    const tangential = vector.x * Math.cos(angle) + vector.y * Math.sin(angle)
    return new VectorProjections(angle, normal, tangential)
  }

  static copyOf(vectorProjections: VectorProjections): VectorProjections {
    return new VectorProjections(vectorProjections.angle, vectorProjections.normal, vectorProjections.tangential)
  }

  private getX(): number {
    return this.normal * Math.sin(this.angle) + this.tangential * Math.cos(this.angle)
  }

  private getY(): number {
    return -this.normal * Math.cos(this.angle) + this.tangential * Math.sin(this.angle)
  }
}

export class Polygon {
  private sidesMap: Map<Segment, Segment>

  constructor(trapeze: Trapeze) {
    const bottom = trapeze.bottom()
    const right = trapeze.right()
    const top = trapeze.top()
    const left = trapeze.left()

    this.sidesMap = new Map()
    this.sidesMap.set(bottom, right)
    this.sidesMap.set(right, top)
    this.sidesMap.set(top, left)
    this.sidesMap.set(left, bottom)
  }

  public sides(): Set<Segment> {
    return new Set(this.sidesMap.keys())
  }

  public vertices(): Set<Position> {
    return new Set(this.sidesMap.keys().map(side => side.begin))
  }

  public next(segment: Segment): Segment | undefined {
    return this.sidesMap.get(segment)
  }
}
