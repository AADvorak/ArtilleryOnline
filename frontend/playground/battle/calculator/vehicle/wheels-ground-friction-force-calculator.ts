import type {ForceCalculator} from "~/playground/battle/calculator/force-calculator";
import {type VehicleCalculations, type WheelCalculations} from "~/playground/data/calculations";
import type {BattleModel, VehicleModel} from "~/playground/data/model";
import {BodyForce} from "~/playground/battle/calculator/body-force";
import {VectorUtils} from "~/playground/utils/vector-utils";
import {BodyUtils} from "~/playground/utils/body-utils";

export class WheelsGroundFrictionForceCalculator implements ForceCalculator<VehicleCalculations> {
  private static readonly FORCE_DESCRIPTION = 'Wheel Ground Friction'

  calculate(calculations: VehicleCalculations, battleModel: BattleModel): BodyForce[] {
    const groundFrictionCoefficient = battleModel.room.specs.groundFrictionCoefficient
    const gravityAcceleration = battleModel.room.specs.gravityAcceleration
    const contactsNumber = BodyUtils.getAllGroundContacts(calculations).length
    const forces: BodyForce[] = []

    this.addWheelFriction(forces, calculations.rightWheel, calculations.model, groundFrictionCoefficient, gravityAcceleration, contactsNumber)
    this.addWheelFriction(forces, calculations.leftWheel, calculations.model, groundFrictionCoefficient, gravityAcceleration, contactsNumber)

    return forces
  }

  private addWheelFriction(
      forces: BodyForce[],
      wheelCalculations: WheelCalculations,
      vehicleModel: VehicleModel,
      groundFrictionCoefficient: number,
      gravityAcceleration: number,
      contactsNumber: number
  ): void {
    const contact = wheelCalculations.groundContact
    if (!contact) {
      return
    }
    const coefficient = groundFrictionCoefficient * gravityAcceleration
        * vehicleModel.preCalc.mass * Math.cos(contact.angle) / contactsNumber / 10
    const velocity = VectorUtils.projectionOfOnto(
        wheelCalculations.velocity!,
        VectorUtils.tangential(contact.angle)
    )

    const force = {
      x: -velocity.x * coefficient,
      y: -velocity.y * coefficient
    }

    forces.push(BodyForce.of(
        force,
        wheelCalculations.position!,
        vehicleModel.state.position,
        WheelsGroundFrictionForceCalculator.FORCE_DESCRIPTION
    ))
  }
}
