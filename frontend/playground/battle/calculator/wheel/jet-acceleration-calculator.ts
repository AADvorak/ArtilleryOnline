import type {WheelCalculations} from "~/playground/data/calculations";
import type {VehicleModel} from "~/playground/data/model";
import {JetType, MovingDirection} from "~/playground/data/common";

export const JetAccelerationCalculator = {
  calculate(wheelCalculations: WheelCalculations, vehicleModel: VehicleModel): void {
    const jetState = vehicleModel.state.jetState

    if (!jetState || !jetState.active || jetState.volume <= 0) {
      return
    }

    const jetSpecs = vehicleModel.config.jet
    const acceleration = jetSpecs.acceleration
    const direction = vehicleModel.state.movingDirection
    const angle = vehicleModel.state.position.angle

    if (jetSpecs.type === JetType.VERTICAL) {
      this.calculateVertical(wheelCalculations, acceleration, angle, direction)
    }

    if (jetSpecs.type === JetType.HORIZONTAL) {
      this.calculateHorizontal(wheelCalculations, acceleration, angle, direction)
    }
  },

  calculateVertical(wheelCalculations: WheelCalculations, acceleration: number, angle: number, direction: MovingDirection) {
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
  },

  calculateHorizontal(wheelCalculations: WheelCalculations, acceleration: number, angle: number, direction: MovingDirection) {
    const additionalAngle = Math.PI / 16

    if (direction === MovingDirection.RIGHT) {
      wheelCalculations.jetAcceleration.x = acceleration * Math.cos(angle + additionalAngle)
      wheelCalculations.jetAcceleration.y = acceleration * Math.sin(angle + additionalAngle)
    } else if (direction === MovingDirection.LEFT) {
      wheelCalculations.jetAcceleration.x = acceleration * Math.cos(angle - additionalAngle + Math.PI)
      wheelCalculations.jetAcceleration.y = acceleration * Math.sin(angle - additionalAngle + Math.PI)
    }
  }
}
