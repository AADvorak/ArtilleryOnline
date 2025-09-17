import type {BodyCalculations} from "~/playground/data/calculations";
import type {BattleModel} from "~/playground/data/model";
import type {BodyAccelerationCalculator} from "~/playground/battle/calculator/body-acceleration-calculator";
import {Constants} from "~/playground/data/constants";

export class BodyVelocityCalculator<C extends BodyCalculations> {

  private accelerationCalculator: BodyAccelerationCalculator<C>

  constructor(accelerationCalculator: BodyAccelerationCalculator<C>) {
    this.accelerationCalculator = accelerationCalculator
  }

  recalculateVelocity(calculations: C, battleModel: BattleModel, timeStepSecs: number): void {
    const acceleration = this.accelerationCalculator.calculate(
        calculations,
        battleModel
    )
    const velocity = calculations.model.state.velocity
    const maxRadius = calculations.model.preCalc.maxRadius
    velocity.x += acceleration.x * timeStepSecs
    velocity.y += acceleration.y * timeStepSecs
    velocity.angle += acceleration.angle * timeStepSecs
    if (Math.abs(velocity.x) < Constants.MIN_VELOCITY) {
      velocity.x = 0.0
    }
    if (Math.abs(velocity.y) < Constants.MIN_VELOCITY) {
      velocity.y = 0.0
    }
    if (Math.abs(velocity.angle * maxRadius) < Constants.MIN_VELOCITY) {
      velocity.angle = 0.0
    }
  }
}
