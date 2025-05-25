import type {Force, Position, Vector} from "~/playground/data/common";
import {VectorUtils} from "~/playground/utils/vector-utils";

export class BodyForce {
  moving: Force | null
  rotating: Force | null
  radiusVector: Vector | null
  description: string

  constructor(
      moving: Force | null,
      rotating: Force | null,
      radiusVector: Vector | null,
      description: string
  ) {
    this.moving = moving
    this.rotating = rotating
    this.radiusVector = radiusVector
    this.description = description
  }

  torque(): number {
    if (!this.radiusVector || !this.rotating) {
      return 0
    }
    return VectorUtils.vectorProduct(this.radiusVector, this.rotating)
  }

  static of(
      force: Force,
      position: Position,
      comPosition: Position,
      description: string
  ): BodyForce {
    const radiusVector = VectorUtils.vectorFromTo(comPosition, position)
    const moving = VectorUtils.projectionOfOnto(force, radiusVector)
    const rotating = {
      x: force.x - moving.x,
      y: force.y - moving.y
    }
    return new BodyForce(moving, rotating, radiusVector, description)
  }

  static atCOM(force: Force, description: string): BodyForce {
    return new BodyForce(force, null, null, description)
  }
}