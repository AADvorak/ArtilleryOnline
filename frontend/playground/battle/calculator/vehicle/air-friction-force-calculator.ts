import type {ForceCalculator} from "~/playground/battle/calculator/force-calculator";
import type {VehicleCalculations} from "~/playground/data/calculations";
import type {BattleModel} from "~/playground/data/model";
import {BodyForce} from "~/playground/battle/calculator/body-force";

export class AirFrictionForceCalculator implements ForceCalculator {
  private static readonly FORCE_DESCRIPTION = 'Air Friction'

  calculate(calculations: VehicleCalculations, battleModel: BattleModel): BodyForce[] {
    const frictionCoefficient = battleModel.room.specs.airFrictionCoefficient
    const velocity = calculations.model.state.velocity
    // todo remove mass with size coefficient
    const mass = calculations.model.preCalc.mass

    const frictionForce = {
      x: -velocity.x * Math.abs(velocity.x) * frictionCoefficient * mass,
      y: -velocity.y * Math.abs(velocity.y) * frictionCoefficient * mass
    }

    return [BodyForce.atCOM(frictionForce, AirFrictionForceCalculator.FORCE_DESCRIPTION)]
  }
}
