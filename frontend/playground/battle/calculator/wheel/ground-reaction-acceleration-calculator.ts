import {type WheelCalculations, WheelGroundState} from '@/playground/data/calculations'
import {VectorUtils} from '@/playground/utils/vector-utils'
import type {VehicleModel} from "~/playground/data/model";

export const GroundReactionAccelerationCalculator = {
  calculate(wheelCalculations: WheelCalculations, vehicleModel: VehicleModel, groundReactionCoefficient: number): void {
    if (
        WheelGroundState.FULL_OVER_GROUND === wheelCalculations.groundState ||
        WheelGroundState.FULL_UNDER_GROUND === wheelCalculations.groundState
    ) {
      return
    }

    const groundAngle = wheelCalculations.groundAngle!
    const velocityVerticalProjection = VectorUtils.getVerticalProjection(
        wheelCalculations.velocity!,
        groundAngle
    )

    if (velocityVerticalProjection < 0) {
      const mass = vehicleModel.preCalc.mass
      const accelerationVerticalProjection =
          -velocityVerticalProjection * wheelCalculations.groundDepth! * groundReactionCoefficient / mass

      wheelCalculations.groundReactionAcceleration.x = VectorUtils.getComponentX(
          accelerationVerticalProjection,
          0.0,
          groundAngle
      )
      wheelCalculations.groundReactionAcceleration.y = VectorUtils.getComponentY(
          accelerationVerticalProjection,
          0.0,
          groundAngle
      )
    }
  }
}
