import type {BodyAcceleration} from '@/playground/data/common'
import type {BattleModel} from '@/playground/data/model'
import type {VehicleCalculations} from '@/playground/data/calculations'
import {VehicleUtils} from '@/playground/utils/vehicle-utils'
import {BodyAccelerationCalculator} from "~/playground/battle/calculator/body-acceleration-calculator";
import {GroundFrictionForceCalculator} from "~/playground/battle/calculator/vehicle/ground-friction-force-calculator";
import {GroundReactionForceCalculator} from "~/playground/battle/calculator/vehicle/ground-reaction-force-calculator";
import {GravityForceCalculator} from "~/playground/battle/calculator/vehicle/gravity-force-calculator";
import {JetForceCalculator} from "~/playground/battle/calculator/vehicle/jet-force-calculator";
import {EngineForceCalculator} from "~/playground/battle/calculator/vehicle/engine-force-calculator";

export const VehicleAccelerationCalculator = {
  calculator: new BodyAccelerationCalculator<VehicleCalculations>(
      [
        new GravityForceCalculator(),
        new GroundFrictionForceCalculator(),
        new GroundReactionForceCalculator(),
        new JetForceCalculator(),
        new EngineForceCalculator()
      ]
  ),

  getVehicleAcceleration(vehicle: VehicleCalculations, battleModel: BattleModel): BodyAcceleration {
    VehicleUtils.calculateAllGroundContacts(vehicle, battleModel.room)
    return this.calculator.calculate(vehicle, battleModel)
  },
}
