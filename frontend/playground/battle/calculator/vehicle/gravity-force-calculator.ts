import type {ForceCalculator} from "~/playground/battle/calculator/force-calculator";
import type {VehicleCalculations} from "~/playground/data/calculations";
import type {BattleModel} from "~/playground/data/model";
import {BodyForce} from "~/playground/battle/calculator/body-force";
import type {Contact, Position} from "~/playground/data/common";

class GravityForceCalculator implements ForceCalculator {
  private static readonly FORCE_DESCRIPTION = 'Gravity'

  calculate(calculations: VehicleCalculations, battleModel: BattleModel): BodyForce[] {
    const roomGravityAcceleration = battleModel.room.specs.gravityAcceleration
    const mass = calculations.model.preCalc.mass
    const forces: BodyForce[] = []
    const groundContacts = new Set<Contact>(calculations.groundContacts)

    if (calculations.rightWheel.groundContact) {
      groundContacts.add(calculations.rightWheel.groundContact)
    }
    if (calculations.leftWheel.groundContact) {
      groundContacts.add(calculations.leftWheel.groundContact)
    }

    const comX = calculations.model.state.position.x
    const contactsArray = Array.from(groundContacts)

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
      const groundGravityDepth = 0.7 * battleModel.room.specs.groundMaxDepth

      if (leftContact.depth <= groundGravityDepth && rightContact.depth <= groundGravityDepth) {
        const comPosition = calculations.model.state.position
        this.addGroundForce(forces, leftContact, comPosition, mass, roomGravityAcceleration, groundGravityDepth)
        this.addGroundForce(forces, rightContact, comPosition, mass, roomGravityAcceleration, groundGravityDepth)
      }
    }

    return forces
  }

  private addGroundForce(
      forces: BodyForce[],
      contact: Contact,
      comPosition: Position,
      mass: number,
      roomGravityAcceleration: number,
      groundGravityDepth: number
  ): void {
    const groundAccelerationModule = Math.abs(roomGravityAcceleration * Math.sin(contact.angle)) *
        Math.sqrt(1 - contact.depth / groundGravityDepth) * mass / 2

    forces.push(BodyForce.of(
        {
          x: -groundAccelerationModule * Math.sin(contact.angle),
          y: -groundAccelerationModule * Math.cos(contact.angle)
        },
        contact.position,
        comPosition,
        GravityForceCalculator.FORCE_DESCRIPTION
    ))
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
