import type {MissileModel} from "~/playground/data/model";
import {MissileAccelerationCalculator} from "~/playground/battle/calculator/missile-acceleration-calculator";
import type {BattleCalculations} from "~/playground/data/calculations";

export const MissileProcessor = {
  processStep(missileModel: MissileModel, battle: BattleCalculations, timeStepSecs: number) {
    const acceleration = MissileAccelerationCalculator.calculate(missileModel, battle)
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
