import type {DroneModel} from "~/playground/data/model";
import {DroneAccelerationCalculator} from "~/playground/battle/calculator/drone-acceleration-calculator";
import {DroneTargetCalculator} from "~/playground/battle/calculator/drone-target-calculator";
import {BattleCalculations, DroneCalculations} from "~/playground/data/calculations";

export const DroneProcessor = {
  processStep(droneModel: DroneModel, battle: BattleCalculations, timeStepSecs: number) {
    const droneCalculations: DroneCalculations = new DroneCalculations(droneModel);
    DroneTargetCalculator.calculate(droneCalculations, battle)

    const acceleration = DroneAccelerationCalculator.calculate(droneCalculations, battle.model, timeStepSecs)
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
