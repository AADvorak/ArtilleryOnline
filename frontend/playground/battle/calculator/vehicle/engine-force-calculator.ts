import type {ForceCalculator} from "~/playground/battle/calculator/force-calculator";
import {type VehicleCalculations, type WheelCalculations} from "~/playground/data/calculations";
import type {BattleModel, VehicleModel} from "~/playground/data/model";
import {BodyForce} from "~/playground/battle/calculator/body-force";
import {JetType, MovingDirection, zeroVector} from "~/playground/data/common";
import {BodyUtils} from "~/playground/utils/body-utils";

export class EngineForceCalculator implements ForceCalculator<VehicleCalculations> {
  private static readonly FORCE_DESCRIPTION = 'Engine'

  calculate(calculations: VehicleCalculations, battleModel: BattleModel): BodyForce[] {
    const forces: BodyForce[] = []
    const contactsNumber = BodyUtils.getAllGroundContacts(calculations).length
    const leftWheelForce = this.calculateForWheel(calculations.leftWheel, calculations.model, contactsNumber)
    const rightWheelForce = this.calculateForWheel(calculations.rightWheel, calculations.model, contactsNumber)

    if (leftWheelForce !== null) {
      forces.push(leftWheelForce)
    }

    if (rightWheelForce !== null) {
      forces.push(rightWheelForce)
    }

    return forces
  }

  private calculateForWheel(calculations: WheelCalculations, vehicleModel: VehicleModel,
                            contactsNumber: number): BodyForce | null {
    const jetState = vehicleModel.state.jetState
    const jetSpecs = vehicleModel.config.jet
    const direction = vehicleModel.state.movingDirection
    const contact = calculations.groundContact
    if (
        direction === null ||
        vehicleModel.state.trackState.broken ||
        (jetSpecs !== null && jetState !== null && jetState.active && jetSpecs.type === JetType.VERTICAL) ||
        !contact
    ) {
      return null
    }

    const wheelRadius = vehicleModel.specs.wheelRadius
    const forceMagnitude = vehicleModel.preCalc.mass * vehicleModel.specs.acceleration
        * Math.cos(contact.angle) / contactsNumber
    const depthAngle = (contact.depth * Math.PI) / (4 * wheelRadius)
    const force = zeroVector()

    if (direction === MovingDirection.RIGHT) {
      force.x = forceMagnitude * Math.cos(contact.angle + depthAngle)
      force.y = forceMagnitude * Math.sin(contact.angle + depthAngle)
    }

    if (direction === MovingDirection.LEFT) {
      force.x = -forceMagnitude * Math.cos(contact.angle - depthAngle)
      force.y = -forceMagnitude * Math.sin(contact.angle - depthAngle)
    }

    return BodyForce.of(
        force,
        contact.position,
        vehicleModel.state.position,
        EngineForceCalculator.FORCE_DESCRIPTION
    )
  }
}
