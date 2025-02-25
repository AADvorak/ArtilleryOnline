import type {BattleModel, DroneModel} from "~/playground/data/model";
import {DroneAccelerationCalculator} from "~/playground/battle/calculator/drone-acceleration-calculator";
import {DroneTargetCalculator} from "~/playground/battle/calculator/drone-target-calculator";

export const DroneProcessor = {
  processStep(droneModel: DroneModel, battleModel: BattleModel, timeStepSecs: number) {
    const droneCalculations = {
      model: droneModel
    }
    DroneTargetCalculator.calculate(droneCalculations, battleModel)

    const acceleration = DroneAccelerationCalculator.calculate(droneCalculations, battleModel, timeStepSecs)
    const velocity = droneModel.state.velocity
    const position = droneModel.state.position

    position.x += velocity.x * timeStepSecs
    position.y += velocity.y * timeStepSecs
    position.angle += velocity.angle * timeStepSecs

    velocity.x += acceleration.x * timeStepSecs
    velocity.y += acceleration.y * timeStepSecs
    velocity.angle += acceleration.angle * timeStepSecs
  }
}
