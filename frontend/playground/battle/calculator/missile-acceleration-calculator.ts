import type {BattleModel, MissileModel} from "~/playground/data/model";
import type {Acceleration, BodyAcceleration} from "~/playground/data/common";
import {VectorUtils} from "~/playground/utils/vector-utils";
import {
  CorrectingAccelerationCalculator
} from "~/playground/battle/calculator/missile/correcting-acceleration-calculator";

export const MissileAccelerationCalculator = {
  calculate(missileModel: MissileModel, battleModel: BattleModel): BodyAcceleration {
    const gravity = {
      x: 0.0,
      y: -battleModel.room.specs.gravityAcceleration
    }
    const pushing = this.calculatePushing(missileModel)
    const movingSum = VectorUtils.sumOf(gravity, pushing)

    const correcting = CorrectingAccelerationCalculator.calculate(missileModel, battleModel)

    const bodyAcceleration = {
      x: movingSum.x,
      y: movingSum.y,
      angle: correcting
    }
    const friction = this.calculateFriction(missileModel, battleModel.room.specs.airFrictionCoefficient)
    return VectorUtils.sumOfBody(bodyAcceleration, friction)
  },

  calculatePushing(missileModel: MissileModel): Acceleration {
    const angle = missileModel.state.position.angle
    const pushingMagnitude = missileModel.specs.pushingAcceleration
    return {
      x: pushingMagnitude * Math.cos(angle),
      y: pushingMagnitude * Math.sin(angle)
    }
  },

  calculateFriction(missileModel: MissileModel, frictionCoefficient: number): BodyAcceleration {
    const velocity = missileModel.state.velocity
    const positionAngle = missileModel.state.position.angle
    const velocityAngle = VectorUtils.getAngle(velocity)
    const diffAngle = positionAngle - velocityAngle
    const resultCoefficient = frictionCoefficient * 2 * (1 + 6 * Math.abs(Math.sin(diffAngle)))

    return {
      x: -velocity.x * Math.abs(velocity.x) * resultCoefficient,
      y: -velocity.y * Math.abs(velocity.y) * resultCoefficient,
      angle: -2.0 * velocity.angle
    }
  }
}
