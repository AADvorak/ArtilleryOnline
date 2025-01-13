import { type WheelCalculations, WheelGroundState } from '@/playground/data/calculations'

export const GravityAccelerationCalculator = {
  calculate(
      wheelCalculations: WheelCalculations,
      roomGravityAcceleration: number,
      groundGravityDepth: number
  ): void {
    if (WheelGroundState.FULL_OVER_GROUND === wheelCalculations.groundState) {
      wheelCalculations.gravityAcceleration.x = 0.0
      wheelCalculations.gravityAcceleration.y = -roomGravityAcceleration
    } else if (
      WheelGroundState.HALF_OVER_GROUND === wheelCalculations.groundState &&
      wheelCalculations.groundDepth! <= groundGravityDepth
    ) {
      const groundAngle = wheelCalculations.groundAngle!
      const groundAccelerationModule = Math.abs(roomGravityAcceleration * Math.sin(groundAngle))
      wheelCalculations.gravityAcceleration.x = -groundAccelerationModule * Math.sin(groundAngle)
      wheelCalculations.gravityAcceleration.y = -groundAccelerationModule * Math.cos(groundAngle)
    }
  }
}
