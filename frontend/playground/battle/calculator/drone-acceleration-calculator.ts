import type {DroneCalculations} from "~/playground/data/calculations";
import type {BattleModel} from "~/playground/data/model";
import type {BodyAcceleration} from "~/playground/data/common";
import {VectorUtils} from "~/playground/utils/vector-utils";
import {BattleUtils} from "~/playground/utils/battle-utils";

export const DroneAccelerationCalculator = {

  calculate(drone: DroneCalculations, battleModel: BattleModel, timeStepSecs: number): BodyAcceleration {
    const gravity = { x: 0, y: -battleModel.room.specs.gravityAcceleration, angle: 0 }
    const friction = this.calculateFriction(drone, battleModel.room.specs.airFrictionCoefficient)
    const engines = drone.model.state.destroyed ? { x: 0, y: 0, angle: 0 } : this.calculateEngines(drone, battleModel, timeStepSecs)
    return VectorUtils.sumOfBody(gravity, friction, engines)
  },

  calculateFriction(drone: DroneCalculations, frictionCoefficient: number): BodyAcceleration {
    const velocity = drone.model.state.velocity
    return {
      x: -velocity.x * Math.abs(velocity.x) * frictionCoefficient,
      y: -velocity.y * Math.abs(velocity.y) * frictionCoefficient,
      angle: -velocity.angle
    }
  },

  calculateEngines(drone: DroneCalculations, battleModel: BattleModel, timeStepSecs: number): BodyAcceleration {
    const angle = drone.model.state.position.angle
    const maxAccelerationMagnitude = drone.model.specs.maxEngineAcceleration
    const accelerationMagnitude = this.getEnginesAccelerationMagnitude(drone, battleModel, timeStepSecs) * Math.cos(angle)
    const accelerationDiff = this.getEnginesAccelerationDiff(drone, battleModel)
    const rightEngineAccelerationMagnitude = this.restricted(accelerationMagnitude + accelerationDiff, maxAccelerationMagnitude)
    const leftEngineAccelerationMagnitude = this.restricted(accelerationMagnitude - accelerationDiff, maxAccelerationMagnitude)
    const rightEngineAcceleration = {
      x: rightEngineAccelerationMagnitude * Math.cos(angle + Math.PI / 2),
      y: rightEngineAccelerationMagnitude * Math.sin(angle + Math.PI / 2)
    }
    const leftEngineAcceleration = {
      x: leftEngineAccelerationMagnitude * Math.cos(angle + Math.PI / 2),
      y: leftEngineAccelerationMagnitude * Math.sin(angle + Math.PI / 2)
    }
    const rightEngineRotatingAcceleration = rightEngineAcceleration.x * Math.sin(angle) + rightEngineAcceleration.y * Math.cos(angle)
    const leftEngineRotatingAcceleration = leftEngineAcceleration.x * Math.sin(angle) + leftEngineAcceleration.y * Math.cos(angle)
    const engineRadius = drone.model.specs.enginesRadius
    return {
      x: (rightEngineAcceleration.x + leftEngineAcceleration.x) / 2,
      y: (rightEngineAcceleration.y + leftEngineAcceleration.y) / 2,
      angle: (rightEngineRotatingAcceleration - leftEngineRotatingAcceleration) / (2 * engineRadius)
    }
  },

  getHeight(drone: DroneCalculations, battleModel: BattleModel) {
    const position = drone.model.state.position
    const nearestGroundPosition = BattleUtils.getNearestGroundPosition(position.x, battleModel.room)
    return position.y - nearestGroundPosition.y
  },

  getEnginesAccelerationMagnitude(drone: DroneCalculations, battleModel: BattleModel, timeStepSecs: number) {
    const flyHeight = drone.model.specs.flyHeight
    const currentHeight = this.getHeight(drone, battleModel)
    const maxAcceleration = drone.model.specs.maxEngineAcceleration
    const gravityAcceleration = battleModel.room.specs.gravityAcceleration
    const landingAcceleration = 0.7 * gravityAcceleration
    if (Object.values(drone.model.state.ammo)[0] == 0 && !drone.target) {
      return maxAcceleration
    } else if (currentHeight > 1.5 * flyHeight) {
      return landingAcceleration
    } else if (currentHeight < 0.5 * flyHeight) {
      return maxAcceleration
    } else {
      const velocityY = drone.model.state.velocity.y
      if (velocityY < -3.0) {
        return maxAcceleration
      }
      const distance = flyHeight - currentHeight
      const absVelocityY = Math.abs(velocityY)
      const absDistance = Math.abs(distance)
      if (absVelocityY < 0.1 && absDistance < 0.01) {
        return gravityAcceleration
      }
      const targetAcceleration = absVelocityY < 0.1
          ? gravityAcceleration + 2 * distance / (timeStepSecs * timeStepSecs)
          : gravityAcceleration - Math.sign(velocityY) * velocityY * velocityY / (2 * distance)
      if (targetAcceleration < 0) {
        return 0.0
      }
      return Math.min(targetAcceleration, maxAcceleration)
    }
  },

  getEnginesAccelerationDiff: function(drone: DroneCalculations, battleModel: BattleModel) {
    const targetAngle = this.getTargetAngle(drone, battleModel)
    const droneAngle = drone.model.state.position.angle
    const angleDiff = BattleUtils.calculateAngleDiff(droneAngle, targetAngle)
    const maxAcceleration = drone.model.specs.maxEngineAcceleration
    if (angleDiff > Math.PI) {
      return maxAcceleration
    } else if (angleDiff < -Math.PI) {
      return -maxAcceleration
    } else {
      return maxAcceleration * angleDiff / Math.PI
    }
  },

  getTargetAngle: function(drone: DroneCalculations, battleModel: BattleModel) {
    const flyHeight = drone.model.specs.flyHeight
    const currentHeight = this.getHeight(drone, battleModel)
    if (currentHeight < 0.5 * flyHeight) {
      return 0.0
    }
    if (!drone.target) {
      return 0.0
    }
    const velocityX = drone.model.state.velocity.x
    const xDiff = drone.target.xDiff
    const criticalAngle = drone.model.specs.criticalAngle
    if (velocityX * xDiff > 0 && Math.abs(xDiff) / Math.abs(velocityX) < 1.0) {
      return 0.0
    }
    return -Math.sign(xDiff) * criticalAngle
  },

  restricted: function(value: number, maxValue: number) {
    if (value > maxValue) {
      return maxValue
    }
    if (value < 0) {
      return 0.0
    }
    return value
  }
}
