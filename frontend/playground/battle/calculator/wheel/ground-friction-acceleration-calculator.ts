import {type WheelCalculations, WheelGroundState} from '@/playground/data/calculations'
import type {VehicleModel} from '@/playground/data/model'

export const GroundFrictionAccelerationCalculator = {
  calculate(
      wheelCalculations: WheelCalculations,
      vehicleModel: VehicleModel,
      groundFrictionCoefficient: number
  ): void {
    if (WheelGroundState.FULL_OVER_GROUND === wheelCalculations.groundState) {
      return
    }

    const depth =
        WheelGroundState.FULL_UNDER_GROUND === wheelCalculations.groundState
            ? 2 * vehicleModel.specs.wheelRadius
            : wheelCalculations.groundDepth!
    const mass = vehicleModel.preCalc.mass

    wheelCalculations.groundFrictionAcceleration.x =
        -wheelCalculations.velocity!.x * depth * groundFrictionCoefficient / mass
    wheelCalculations.groundFrictionAcceleration.y =
        -wheelCalculations.velocity!.y * depth * groundFrictionCoefficient / mass
  }
}
