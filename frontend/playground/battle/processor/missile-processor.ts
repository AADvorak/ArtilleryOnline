import type {BattleModel, MissileModel} from "~/playground/data/model";
import {MissileAccelerationCalculator} from "~/playground/battle/calculator/missile-acceleration-calculator";

export const MissileProcessor = {
  processStep(missileModel: MissileModel, battleModel: BattleModel, timeStepSecs: number) {
    const acceleration = MissileAccelerationCalculator.calculate(missileModel, battleModel)
    const velocity = missileModel.state.velocity
    const position = missileModel.state.position

    position.x += velocity.x * timeStepSecs
    position.y += velocity.y * timeStepSecs
    position.angle += velocity.angle * timeStepSecs

    velocity.x += acceleration.x * timeStepSecs
    velocity.y += acceleration.y * timeStepSecs
    velocity.angle += acceleration.angle * timeStepSecs
  }
}
