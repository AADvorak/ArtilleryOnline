import type {ForceCalculator} from "~/playground/battle/calculator/force-calculator";
import type {BodyCalculations} from "~/playground/data/calculations";
import type {BattleModel} from "~/playground/data/model";
import {BodyForce} from "~/playground/battle/calculator/body-force";
import {VectorUtils} from "~/playground/utils/vector-utils";
import {BodyUtils} from "~/playground/utils/body-utils";

export class GroundReactionForceCalculator implements ForceCalculator<BodyCalculations> {
  private static readonly FORCE_DESCRIPTION = 'Ground reaction'

  calculate(calculations: BodyCalculations, battleModel: BattleModel): BodyForce[] {
    const groundReactionCoefficient = battleModel.room.specs.groundReactionCoefficient
    const groundMaxDepth = battleModel.room.specs.groundMaxDepth
    const forces: BodyForce[] = []
    this.addReaction(forces, calculations, groundReactionCoefficient, groundMaxDepth)
    return forces
  }

  private addReaction(
      forces: BodyForce[],
      calculations: BodyCalculations,
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
