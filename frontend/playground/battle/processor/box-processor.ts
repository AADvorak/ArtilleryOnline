import type {BattleModel, BoxModel} from '@/playground/data/model'
import {BattleCalculations, BoxCalculations} from '@/playground/data/calculations'
import {BodyAccelerationCalculator} from "~/playground/battle/calculator/body-acceleration-calculator";
import {GravityForceCalculator} from "~/playground/battle/calculator/common/gravity-force-calculator";
import {BodyVelocityCalculator} from "~/playground/battle/calculator/body-velocity-calculator";
import {BodyUtils} from "~/playground/utils/body-utils";
import {GroundFrictionForceCalculator} from "~/playground/battle/calculator/common/ground-friction-force-calculator";

export const BoxProcessor = {

  velocityCalculator: new BodyVelocityCalculator(
      new BodyAccelerationCalculator<BoxCalculations>(
          [
            new GravityForceCalculator(),
            new GroundFrictionForceCalculator()
          ]
      )
  ),

  processBeforeCollision(box: BoxCalculations, battle: BattleCalculations) {
    box.calculateAllGroundContacts(battle.model.room)
    this.velocityCalculator.recalculateVelocity(box, battle.model, battle.timeStepSecs)
    box.calculateNextPosition(battle.timeStepSecs)
  },

  processAfterCollision(box: BoxCalculations, battle: BattleCalculations) {
    box.applyNextPosition()
  },

  /**
   * @deprecated
   */
  processStep(boxModel: BoxModel, battleModel: BattleModel, timeStepSecs: number) {
    const calculations = new BoxCalculations(boxModel)
    this.recalculateVelocity(calculations, battleModel, timeStepSecs)
    BodyUtils.recalculateBodyPosition(boxModel, timeStepSecs)
  },

  recalculateVelocity(
      calculations: BoxCalculations,
      battleModel: BattleModel,
      timeStepSecs: number
  ) {
    calculations.calculateAllGroundContacts(battleModel.room)
    this.velocityCalculator.recalculateVelocity(calculations, battleModel, timeStepSecs)
  }
}
