import type {BattleModel, BoxModel, RoomModel} from '@/playground/data/model'
import {type BoxCalculations} from '@/playground/data/calculations'
import {BodyAccelerationCalculator} from "~/playground/battle/calculator/body-acceleration-calculator";
import {GravityForceCalculator} from "~/playground/battle/calculator/common/gravity-force-calculator";
import {BodyVelocityCalculator} from "~/playground/battle/calculator/body-velocity-calculator";
import {ShapeNames, type TrapezeShape} from "~/playground/data/shapes";
import {GroundContactUtils} from "~/playground/utils/ground-contact-utils";
import {Trapeze} from "~/playground/data/geometry";
import {BodyUtils} from "~/playground/utils/body-utils";
import {GroundFrictionForceCalculator} from "~/playground/battle/calculator/common/ground-friction-force-calculator";
import {GroundReactionForceCalculator} from "~/playground/battle/calculator/common/ground-reaction-force-calculator";

export const BoxProcessor = {

  velocityCalculator: new BodyVelocityCalculator(
      new BodyAccelerationCalculator<BoxCalculations>(
          [
            new GravityForceCalculator(),
            new GroundFrictionForceCalculator(),
            new GroundReactionForceCalculator()
          ]
      )
  ),

  processStep(boxModel: BoxModel, battleModel: BattleModel, timeStepSecs: number) {
    const calculations = {model: boxModel} as BoxCalculations
    this.recalculateVelocity(calculations, battleModel, timeStepSecs)
    BodyUtils.recalculateBodyPosition(boxModel, timeStepSecs)
  },

  recalculateVelocity(
      calculations: BoxCalculations,
      battleModel: BattleModel,
      timeStepSecs: number
  ) {
    this.calculateGroundContacts(calculations, battleModel.room)
    this.velocityCalculator.recalculateVelocity(calculations, battleModel, timeStepSecs)
  },

  calculateGroundContacts(box: BoxCalculations, roomModel: RoomModel): void {
    const position = BodyUtils.getGeometryBodyPosition(box.model)
    const shape = box.model.specs.shape

    if (shape.name === ShapeNames.TRAPEZE) {
      box.groundContacts = GroundContactUtils.getTrapezeGroundContacts(
          new Trapeze(position, shape as TrapezeShape),
          roomModel
      )
    }
  }
}
