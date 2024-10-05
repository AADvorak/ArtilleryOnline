import { type WheelCalculations, WheelGroundState } from '@/data/calculations'
import { VectorUtils } from '@/utils/vector-utils'

export const GroundReactionAccelerationCalculator = {
  calculate(wheelCalculations: WheelCalculations, groundReactionCoefficient: number): void {
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
      const accelerationVerticalProjection =
        -velocityVerticalProjection * wheelCalculations.groundDepth! * groundReactionCoefficient

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
