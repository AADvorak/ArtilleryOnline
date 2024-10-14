import type {WheelCalculations} from "~/playground/data/calculations";
import type {VehicleModel} from "~/playground/data/model";
import {MovingDirection} from "~/playground/data/common";

export const JetAccelerationCalculator = {
  calculate(wheelCalculations: WheelCalculations, vehicleModel: VehicleModel): void {
    const jetState = vehicleModel.state.jetState

    if (!jetState.active || jetState.volume <= 0) {
      return
    }

    const jetSpecs = vehicleModel.config.jet
    const acceleration = jetSpecs.acceleration
    const direction = vehicleModel.state.movingDirection
    const angle = vehicleModel.state.angle
    const angleCoefficient = 1 + wheelCalculations.sign * Math.sin(angle)

    if (direction == null) {
      wheelCalculations.jetAcceleration.x = 0.0
      wheelCalculations.jetAcceleration.y = acceleration * angleCoefficient
    } else if (direction === MovingDirection.RIGHT) {
      wheelCalculations.jetAcceleration.x = acceleration / Math.sqrt(2)
      wheelCalculations.jetAcceleration.y = acceleration * angleCoefficient / Math.sqrt(2)
    } else if (direction === MovingDirection.LEFT) {
      wheelCalculations.jetAcceleration.x = -acceleration / Math.sqrt(2)
      wheelCalculations.jetAcceleration.y = acceleration * angleCoefficient / Math.sqrt(2)
    }
  }
}
