import type {ForceCalculator} from "~/playground/battle/calculator/force-calculator";
import type {VehicleCalculations, WheelCalculations} from "~/playground/data/calculations";
import type {BattleModel} from "~/playground/data/model";
import {BodyForce} from "~/playground/battle/calculator/body-force";
import {VectorUtils} from "~/playground/utils/vector-utils";
import type {Position} from "~/playground/data/common";
import {BodyUtils} from "~/playground/utils/body-utils";

export class GroundReactionForceCalculator implements ForceCalculator<VehicleCalculations> {
  private static readonly FORCE_DESCRIPTION = 'Ground reaction'

  calculate(calculations: VehicleCalculations, battleModel: BattleModel): BodyForce[] {
    const groundReactionCoefficient = battleModel.room.specs.groundReactionCoefficient
    const groundMaxDepth = battleModel.room.specs.groundMaxDepth
    const comPosition = calculations.model.state.position
    const forces: BodyForce[] = []

    this.addWheelReaction(forces, calculations.rightWheel, comPosition, groundReactionCoefficient, groundMaxDepth)
    this.addWheelReaction(forces, calculations.leftWheel, comPosition, groundReactionCoefficient, groundMaxDepth)
    this.addHullReaction(forces, calculations, groundReactionCoefficient, groundMaxDepth)

    return forces
  }

  private addWheelReaction(
      forces: BodyForce[],
      calculations: WheelCalculations,
      comPosition: Position,
      groundReactionCoefficient: number,
      groundMaxDepth: number
  ): void {
    const groundContact = calculations.groundContact
    if (!groundContact) {
      return
    }

    const velocityNormalProjectionMagnitude = VectorUtils.getMagnitude(
        VectorUtils.projectionOfOnto(calculations.velocity!, groundContact.normal)
    )

    if (velocityNormalProjectionMagnitude > 0) {
      const depth = Math.min(groundContact.depth, groundMaxDepth)
      const force = VectorUtils.multiply(groundContact.normal,
          -velocityNormalProjectionMagnitude * depth * groundReactionCoefficient)

      forces.push(BodyForce.of(
          force,
          groundContact.position,
          comPosition,
          GroundReactionForceCalculator.FORCE_DESCRIPTION
      ))
    }
  }

  private addHullReaction(
      forces: BodyForce[],
      calculations: VehicleCalculations,
      groundReactionCoefficient: number,
      groundMaxDepth: number
  ): void {
    const groundContacts = calculations.groundContacts
    if (!groundContacts) {
      return
    }
    const comPosition = calculations.model.state.position

    groundContacts.forEach(contact => {
      const velocityNormalProjectionMagnitude = VectorUtils.getMagnitude(VectorUtils.projectionOfOnto(
          BodyUtils.getVelocityAt(calculations.model.state, contact.position), contact.normal))
      if (velocityNormalProjectionMagnitude > 0) {
        const depth = Math.min(contact.depth, groundMaxDepth)
        const force = VectorUtils.multiply(contact.normal,
            -velocityNormalProjectionMagnitude * depth * groundReactionCoefficient)

        forces.push(BodyForce.of(
            force,
            contact.position,
            comPosition,
            GroundReactionForceCalculator.FORCE_DESCRIPTION
        ))
      }
    })
  }
}
