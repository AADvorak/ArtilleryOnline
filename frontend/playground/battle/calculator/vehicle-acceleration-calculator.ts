import type {BodyAcceleration} from '@/playground/data/common'
import type {RoomModel, VehicleModel} from '@/playground/data/model'
import type {VehicleCalculations, WheelCalculations} from '@/playground/data/calculations'
import {VehicleUtils} from '@/playground/utils/vehicle-utils'
import {VectorUtils} from '@/playground/utils/vector-utils'
import {GroundPositionCalculator} from '@/playground/battle/calculator/wheel/ground-position-calculator'
import {GroundStateCalculator} from '@/playground/battle/calculator/wheel/ground-state-calculator'
import {EngineAccelerationCalculator} from '@/playground/battle/calculator/wheel/engine-acceleration-calculator'
import {
  GroundFrictionAccelerationCalculator
} from '@/playground/battle/calculator/wheel/ground-friction-acceleration-calculator'
import {
  GroundReactionAccelerationCalculator
} from '@/playground/battle/calculator/wheel/ground-reaction-acceleration-calculator'
import {GravityAccelerationCalculator} from '@/playground/battle/calculator/wheel/gravity-acceleration-calculator'
import {JetAccelerationCalculator} from "~/playground/battle/calculator/wheel/jet-acceleration-calculator";

export const VehicleAccelerationCalculator = {
  getVehicleAcceleration(
      calculations: VehicleCalculations,
      vehicleModel: VehicleModel,
      roomModel: RoomModel
  ): BodyAcceleration {
    VehicleUtils.calculateWheelVelocity(vehicleModel, calculations.rightWheel)
    VehicleUtils.calculateWheelVelocity(vehicleModel, calculations.leftWheel)

    calculations.rightWheel.position = VehicleUtils.getRightWheelPosition(vehicleModel)
    calculations.leftWheel.position = VehicleUtils.getLeftWheelPosition(vehicleModel)

    this.calculateWheelAcceleration(calculations.rightWheel, vehicleModel, roomModel)
    this.calculateWheelAcceleration(calculations.leftWheel, vehicleModel, roomModel)

    this.calculateWheelSumAcceleration(calculations.rightWheel)
    this.calculateWheelSumAcceleration(calculations.leftWheel)

    const rotatingAcceleration = this.getVehicleRotatingAcceleration(calculations, vehicleModel.state.position.angle)
    const wheelsMovingAcceleration = {
      x:
          (calculations.rightWheel.sumAcceleration!.x + calculations.leftWheel.sumAcceleration!.x) / 2,
      y:
          (calculations.rightWheel.sumAcceleration!.y + calculations.leftWheel.sumAcceleration!.y) / 2
    }

    const vehicleVelocity = vehicleModel.state.velocity
    const frictionCoefficient = roomModel.specs.airFrictionCoefficient
    const frictionAcceleration = {
      x: -vehicleVelocity.x * Math.abs(vehicleVelocity.x) * frictionCoefficient,
      y: -vehicleVelocity.y * Math.abs(vehicleVelocity.y) * frictionCoefficient
    }

    const movingAcceleration = VectorUtils.sumOf(wheelsMovingAcceleration, frictionAcceleration)

    return {
      x: movingAcceleration.x,
      y: movingAcceleration.y,
      angle: rotatingAcceleration / vehicleModel.specs.radius - vehicleVelocity.angle
    }
  },

  calculateWheelSumAcceleration(wheelCalculations: WheelCalculations): void {
    wheelCalculations.sumAcceleration = VectorUtils.sumOf(
        wheelCalculations.gravityAcceleration,
        wheelCalculations.engineAcceleration,
        wheelCalculations.groundFrictionAcceleration,
        wheelCalculations.groundReactionAcceleration,
        wheelCalculations.jetAcceleration
    )
  },

  getVehicleRotatingAcceleration(calculations: VehicleCalculations, angle: number): number {
    const rightWheelRotatingAcceleration =
        calculations.rightWheel.sumAcceleration!.x * Math.sin(angle) +
        calculations.rightWheel.sumAcceleration!.y * Math.cos(angle)
    const leftWheelRotatingAcceleration =
        calculations.leftWheel.sumAcceleration!.x * Math.sin(angle) +
        calculations.leftWheel.sumAcceleration!.y * Math.cos(angle)
    return (rightWheelRotatingAcceleration - leftWheelRotatingAcceleration) / 2
        + this.getReturnOnWheelsRotatingAcceleration(angle)
  },

  getReturnOnWheelsRotatingAcceleration(angle: number) {
    if (angle >= Math.PI / 2 - 0.1) {
      return -2.0;
    }
    if (angle <= -Math.PI / 2 + 0.1) {
      return 2.0;
    }
    return 0.0;
  },

  calculateWheelAcceleration(
      wheelCalculations: WheelCalculations,
      vehicleModel: VehicleModel,
      roomModel: RoomModel
  ): void {
    const roomGravityAcceleration = roomModel.specs.gravityAcceleration
    const groundReactionCoefficient = roomModel.specs.groundReactionCoefficient
    const groundFrictionCoefficient = roomModel.specs.groundFrictionCoefficient
    const groundGravityDepth = 0.7 * roomModel.specs.groundMaxDepth
    const wheelRadius = vehicleModel.specs.wheelRadius

    GroundPositionCalculator.calculate(wheelCalculations, wheelRadius, roomModel)
    GroundStateCalculator.calculate(wheelCalculations)

    EngineAccelerationCalculator.calculate(wheelCalculations, vehicleModel)
    JetAccelerationCalculator.calculate(wheelCalculations, vehicleModel)
    GroundFrictionAccelerationCalculator.calculate(
        wheelCalculations,
        vehicleModel,
        groundFrictionCoefficient
    )
    GroundReactionAccelerationCalculator.calculate(
        wheelCalculations,
        vehicleModel,
        groundReactionCoefficient
    )
    GravityAccelerationCalculator.calculate(
        wheelCalculations,
        roomGravityAcceleration,
        groundGravityDepth
    )
  }
}
