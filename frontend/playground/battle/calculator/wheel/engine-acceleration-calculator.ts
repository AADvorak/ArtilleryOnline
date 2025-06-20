import {type WheelCalculations, WheelGroundState} from '@/playground/data/calculations'
import type {VehicleModel} from '@/playground/data/model'
import {JetType, MovingDirection} from '@/playground/data/common'

export const EngineAccelerationCalculator = {
  calculate(wheelCalculations: WheelCalculations, vehicleModel: VehicleModel): void {
    const jetState = vehicleModel.state.jetState
    const jetSpecs = vehicleModel.config.jet
    if (
        vehicleModel.state.trackState.broken ||
        jetSpecs && jetState && jetState.active && JetType.VERTICAL === jetSpecs.type ||
        WheelGroundState.FULL_UNDER_GROUND === wheelCalculations.groundState ||
        WheelGroundState.FULL_OVER_GROUND === wheelCalculations.groundState
    ) {
      return
    }

    const depth = wheelCalculations.groundDepth!
    const groundAngle = wheelCalculations.groundAngle!
    const direction = vehicleModel.state.movingDirection
    const wheelRadius = vehicleModel.specs.wheelRadius
    const depthCoefficient = 1 - (depth * 0.5) / wheelRadius
    const acceleration = (depthCoefficient * vehicleModel.specs.acceleration) / 2
    const depthAngle = (depth * Math.PI) / (4 * wheelRadius)

    if (MovingDirection.RIGHT === direction) {
      wheelCalculations.engineAcceleration.x = acceleration * Math.cos(groundAngle + depthAngle)
      wheelCalculations.engineAcceleration.y = acceleration * Math.sin(groundAngle + depthAngle)
    }

    if (MovingDirection.LEFT === direction) {
      wheelCalculations.engineAcceleration.x = -acceleration * Math.cos(groundAngle - depthAngle)
      wheelCalculations.engineAcceleration.y = -acceleration * Math.sin(groundAngle - depthAngle)
    }
  }
}
