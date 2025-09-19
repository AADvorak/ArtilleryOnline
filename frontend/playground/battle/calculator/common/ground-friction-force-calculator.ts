import type {ForceCalculator} from "~/playground/battle/calculator/force-calculator";
import {type BodyCalculations} from "~/playground/data/calculations";
import type {BattleModel} from "~/playground/data/model";
import {BodyForce} from "~/playground/battle/calculator/body-force";
import {VectorUtils} from "~/playground/utils/vector-utils";
import {BodyUtils} from "~/playground/utils/body-utils";

export class GroundFrictionForceCalculator implements ForceCalculator<BodyCalculations> {
  private static readonly FORCE_DESCRIPTION = 'Ground Friction'

  calculate(calculations: BodyCalculations, battleModel: BattleModel): BodyForce[] {
    const groundFrictionCoefficient = battleModel.room.specs.groundFrictionCoefficient
    const gravityAcceleration = battleModel.room.specs.gravityAcceleration
    const contactsNumber = BodyUtils.getAllGroundContacts(calculations).length
    const forces: BodyForce[] = []
    this.addFriction(forces, calculations, groundFrictionCoefficient, gravityAcceleration, contactsNumber)
    return forces
  }

  private addFriction(
      forces: BodyForce[],
      calculations: BodyCalculations,
      groundFrictionCoefficient: number,
      gravityAcceleration: number,
      contactsNumber: number
  ): void {
    const groundContacts = calculations.groundContacts
    if (!groundContacts) {
      return
    }

    const forceMultiplier = groundFrictionCoefficient * gravityAcceleration
        * calculations.model.preCalc.mass / contactsNumber

    groundContacts.forEach(contact => {
      const tangential = VectorUtils.tangential(contact.angle)
      const movingVelocity = VectorUtils.projectionOfOnto(calculations.model.state.velocity, tangential)
      const rotatingVelocity = VectorUtils.projectionOfOnto(
          BodyUtils.getRotatingVelocityAt(calculations.model.state, contact.position), tangential
      )
      const movingVelocityMagnitude = VectorUtils.getMagnitude(movingVelocity)
      const rotatingVelocityMagnitude = VectorUtils.getMagnitude(rotatingVelocity)
      const contactForceMultiplier = forceMultiplier * Math.cos(contact.angle)

      if (movingVelocityMagnitude > rotatingVelocityMagnitude) {
        const velocity = BodyUtils.getVelocityAt(calculations.model.state, contact.position)
        const movingForce = {
          x: -velocity.x * contactForceMultiplier,
          y: -velocity.y * contactForceMultiplier
        }
        forces.push(BodyForce.of(
            movingForce,
            contact.position,
            calculations.model.state.position,
            GroundFrictionForceCalculator.FORCE_DESCRIPTION
        ))
      } else {
        const rotatingForce = {
          x: -rotatingVelocity.x * contactForceMultiplier,
          y: -rotatingVelocity.y * contactForceMultiplier
        }
        forces.push(BodyForce.atCOM(rotatingForce, GroundFrictionForceCalculator.FORCE_DESCRIPTION))
      }
    })
  }
}
