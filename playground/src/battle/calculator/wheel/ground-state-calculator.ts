import { type WheelCalculations, WheelGroundState } from '@/data/calculations'

export const GroundStateCalculator = {
  calculate(wheelCalculations: WheelCalculations): void {
    if (wheelCalculations.nearestGroundPoint == null) {
      if (wheelCalculations.nearestGroundPointByX!.y >= wheelCalculations.position!.y) {
        wheelCalculations.groundState = WheelGroundState.FULL_UNDER_GROUND
      } else {
        wheelCalculations.groundState = WheelGroundState.FULL_OVER_GROUND
      }
    } else {
      if (wheelCalculations.nearestGroundPointByX!.y >= wheelCalculations.position!.y) {
        wheelCalculations.groundState = WheelGroundState.HALF_UNDER_GROUND
      } else {
        wheelCalculations.groundState = WheelGroundState.HALF_OVER_GROUND
      }
    }
  }
}
