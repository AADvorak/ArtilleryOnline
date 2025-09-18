import type {ForceCalculator} from "~/playground/battle/calculator/force-calculator";
import type {BodyCalculations} from "~/playground/data/calculations";
import type {BattleModel} from "~/playground/data/model";
import {BodyForce} from "~/playground/battle/calculator/body-force";
import type {Contact} from "~/playground/data/common";
import {BodyUtils} from "~/playground/utils/body-utils";

export class GravityForceCalculator implements ForceCalculator<BodyCalculations> {
  private static readonly FORCE_DESCRIPTION = 'Gravity'

  calculate(calculations: BodyCalculations, battleModel: BattleModel): BodyForce[] {
    const roomGravityAcceleration = battleModel.room.specs.gravityAcceleration
    const mass = calculations.model.preCalc.mass
    const forces: BodyForce[] = []

    const comX = calculations.model.state.position.x
    const contactsArray = BodyUtils.getAllGroundContacts(calculations)

    const leftContacts = contactsArray.filter(c => c.position.x < comX)
    const rightContacts = contactsArray.filter(c => c.position.x > comX)

    if (leftContacts.length === 0 || rightContacts.length === 0) {
      forces.push(BodyForce.atCOM(
          {x: 0, y: -roomGravityAcceleration * mass},
          GravityForceCalculator.FORCE_DESCRIPTION
      ))
    } else {
      const leftContact = this.getFarthest(leftContacts, comX)
      const rightContact = this.getFarthest(rightContacts, comX)
      this.addGroundForce(forces, leftContact, mass, roomGravityAcceleration)
      this.addGroundForce(forces, rightContact, mass, roomGravityAcceleration)
    }

    return forces
  }

  private addGroundForce(
      forces: BodyForce[],
      contact: Contact,
      mass: number,
      roomGravityAcceleration: number
  ): void {
    const maxFriction = 1.0
    const forceModule = Math.max(
        Math.abs(roomGravityAcceleration * Math.sin(contact.angle)) * mass / 2 - maxFriction,
        0.0
    )

    if (forceModule > 0) {
      forces.push(BodyForce.atCOM(
          {
            x: -forceModule * Math.sin(contact.angle),
            y: -forceModule * Math.cos(contact.angle)
          },
          GravityForceCalculator.FORCE_DESCRIPTION
      ))
    }
  }

  private getFarthest(contacts: Contact[], comX: number): Contact {
    let farthest = contacts[0]
    let farthestDistance = this.xDistance(farthest, comX)

    for (const contact of contacts) {
      const currentDistance = this.xDistance(contact, comX)
      if (currentDistance > farthestDistance) {
        farthest = contact
        farthestDistance = currentDistance
      }
    }

    return farthest
  }

  private xDistance(contact: Contact, comX: number): number {
    return Math.abs(contact.position.x - comX)
  }
}
