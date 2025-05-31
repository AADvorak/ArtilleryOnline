import type {ForceCalculator} from "~/playground/battle/calculator/force-calculator";
import {type VehicleCalculations, type WheelCalculations} from "~/playground/data/calculations";
import type {BattleModel, VehicleModel} from "~/playground/data/model";
import {BodyForce} from "~/playground/battle/calculator/body-force";
import {JetType, MovingDirection, zeroVector} from "~/playground/data/common";

export class EngineForceCalculator implements ForceCalculator<VehicleCalculations> {
  private static readonly FORCE_DESCRIPTION = 'Engine'

  calculate(calculations: VehicleCalculations, battleModel: BattleModel): BodyForce[] {
    const forces: BodyForce[] = []

    const leftWheelForce = this.calculateForWheel(calculations.leftWheel, calculations.model)
    const rightWheelForce = this.calculateForWheel(calculations.rightWheel, calculations.model)

    if (leftWheelForce !== null) {
      forces.push(leftWheelForce)
    }

    if (rightWheelForce !== null) {
      forces.push(rightWheelForce)
    }

    return forces
  }

  private calculateForWheel(calculations: WheelCalculations, vehicleModel: VehicleModel): BodyForce | null {
    const jetState = vehicleModel.state.jetState
    const jetSpecs = vehicleModel.config.jet
    const direction = vehicleModel.state.movingDirection

    if (
        direction === null ||
        vehicleModel.state.trackState.broken ||
        (jetSpecs !== null && jetState !== null && jetState.active && jetSpecs.type === JetType.VERTICAL) ||
        !calculations.groundContact
    ) {
      return null
    }

    const depth = calculations.groundContact.depth
    const groundAngle = calculations.groundContact.angle
    const wheelRadius = vehicleModel.specs.wheelRadius
    const forceMagnitude = vehicleModel.preCalc.mass * vehicleModel.specs.acceleration / 2
    const depthAngle = (depth * Math.PI) / (4 * wheelRadius)
    const force = zeroVector()

    if (direction === MovingDirection.RIGHT) {
      force.x = forceMagnitude * Math.cos(groundAngle + depthAngle)
      force.y = forceMagnitude * Math.sin(groundAngle + depthAngle)
    }

    if (direction === MovingDirection.LEFT) {
      force.x = -forceMagnitude * Math.cos(groundAngle - depthAngle)
      force.y = -forceMagnitude * Math.sin(groundAngle - depthAngle)
    }

    return BodyForce.of(
        force,
        calculations.groundContact.position,
        vehicleModel.state.position,
        EngineForceCalculator.FORCE_DESCRIPTION
    )
  }
}
