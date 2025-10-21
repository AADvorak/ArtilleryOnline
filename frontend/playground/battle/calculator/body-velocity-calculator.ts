import type {BodyCalculations} from "~/playground/data/calculations";
import type {BattleModel, BodyPreCalc} from "~/playground/data/model";
import type {BodyAccelerationCalculator} from "~/playground/battle/calculator/body-acceleration-calculator";
import {Constants} from "~/playground/data/constants";
import type {BodyVelocity} from "~/playground/data/common";

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
    const preCalc = calculations.model.preCalc
    velocity.x += acceleration.x * timeStepSecs
    velocity.y += acceleration.y * timeStepSecs
    velocity.angle += acceleration.angle * timeStepSecs
    this.fade(velocity, battleModel.room.specs.airFrictionCoefficient, timeStepSecs, preCalc)
    if (Math.abs(velocity.x) < Constants.MIN_VELOCITY) {
      velocity.x = 0.0
    } else if (Math.abs(velocity.x) > Constants.MAX_VELOCITY) {
      velocity.x = Constants.MAX_VELOCITY * Math.sign(velocity.x)
    }
    if (Math.abs(velocity.y) < Constants.MIN_VELOCITY) {
      velocity.y = 0.0
    } else if (Math.abs(velocity.y) > Constants.MAX_VELOCITY) {
      velocity.y = Constants.MAX_VELOCITY * Math.sign(velocity.y)
    }
    if (Math.abs(velocity.angle * preCalc.maxRadius) < Constants.MIN_VELOCITY) {
      velocity.angle = 0.0
    } else if (Math.abs(velocity.angle) > Constants.MAX_VELOCITY) {
      velocity.angle = Constants.MAX_VELOCITY * Math.sign(velocity.angle)
    }
  }

  fade(velocity: BodyVelocity, frictionCoefficient: number, timeStep: number, preCalc: BodyPreCalc) {
    const movingCoefficient = 2 * frictionCoefficient * preCalc.maxRadius / preCalc.mass
    const rotatingCoefficient = 2 * Math.PI * frictionCoefficient * Math.pow(preCalc.maxRadius, 2)
        / preCalc.momentOfInertia
    velocity.x = velocity.x / (1 + movingCoefficient * Math.abs(velocity.x) * timeStep)
    velocity.y = velocity.y / (1 + movingCoefficient * Math.abs(velocity.y) * timeStep)
    velocity.angle = velocity.angle / (1 + rotatingCoefficient * Math.abs(velocity.angle) * timeStep)
  }
}
