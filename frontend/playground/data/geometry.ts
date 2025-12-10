import type {BodyPosition, Position, Vector, Velocity} from "~/playground/data/common";
import type {TrapezeShape} from "~/playground/data/shapes";
import {BattleUtils} from "~/playground/utils/battle-utils";
import {VectorUtils} from "~/playground/utils/vector-utils";

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

export class Circle {
  center: Position
  radius: number

  constructor(center: Position, radius: number) {
    this.center = center
    this.radius = radius
  }
}

export class HalfCircle {
  center: Position
  radius: number
  angle: number

  constructor(center: Position, radius: number, angle: number) {
    this.center = center
    this.radius = radius
    this.angle = angle
  }

  chord(): Segment {
    const start: Position = {
      x: this.center.x + this.radius * Math.cos(this.angle),
      y: this.center.y + this.radius * Math.sin(this.angle)
    }

    const end: Position = {
      x: this.center.x - this.radius * Math.cos(this.angle),
      y: this.center.y - this.radius * Math.sin(this.angle)
    }

    return new Segment(start, end)
  }

  circle(): Circle {
    return new Circle(this.center, this.radius)
  }

  static of(bodyPosition: BodyPosition, radius: number): HalfCircle {
    return new HalfCircle({x: bodyPosition.x, y: bodyPosition.y}, radius, bodyPosition.angle)
  }
}

export class Trapeze {
  position: BodyPosition
  shape: TrapezeShape

  constructor(position: BodyPosition, shape: TrapezeShape) {
    this.position = position
    this.shape = shape
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
