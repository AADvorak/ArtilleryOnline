import type {Battle} from "~/playground/data/battle";
import {VehicleProcessor} from "~/playground/battle/processor/vehicle-processor";
import {ShellProcessor} from "~/playground/battle/processor/shell-processor";
import {ExplosionProcessor} from "~/playground/battle/processor/explosion-processor";
import {MissileProcessor} from "~/playground/battle/processor/missile-processor";
import {DroneProcessor} from "~/playground/battle/processor/drone-processor";
import {BoxProcessor} from "~/playground/battle/processor/box-processor";
import {BattleCalculations, VehicleCalculations} from "~/playground/data/calculations";
import type {VehicleModel} from "~/playground/data/model";
import {CollisionsProcessor} from "~/playground/battle/collision/collisions-processor";
import {
  VehicleGroundCollisionsDetector
} from "~/playground/battle/collision/detector/vehicle-ground-collision-detector";

export const useBattleObjectsProcessor = function (
    debug: boolean,
    processCollisions: boolean,
    additionalIterationsNumber: number
){

  const collisionsProcessor = new CollisionsProcessor(
      debug, additionalIterationsNumber,
      [new VehicleGroundCollisionsDetector()], [], []
  )

  function process(battle: Battle, timeStepSecs: number) {
    const battleCalculations = initBattleCalculations(battle, timeStepSecs)
    battleCalculations.vehicles.forEach(vehicle => {
      VehicleProcessor.processBeforeCollision(vehicle, battleCalculations)
    })
    Object.values(battle.model.shells).forEach(shell => {
      ShellProcessor.processStep(shell, timeStepSecs, battle.model.room.specs)
    })
    Object.values(battle.model.explosions).forEach(explosion => {
      ExplosionProcessor.processStep(explosion, timeStepSecs)
    })
    Object.values(battle.model.missiles).forEach(missile => {
      MissileProcessor.processStep(missile, battle.model, timeStepSecs)
    })
    Object.values(battle.model.drones).forEach(drone => {
      DroneProcessor.processStep(drone, battle.model, timeStepSecs)
    })
    Object.values(battle.model.boxes).forEach(box => {
      BoxProcessor.processStep(box, battle.model, timeStepSecs)
    })
    processCollisions && collisionsProcessor.process(battleCalculations)
    battleCalculations.vehicles.forEach(vehicle => {
      VehicleProcessor.processAfterCollision(vehicle, battleCalculations)
    })
  }

  function initBattleCalculations(battle: Battle, timeStepSecs: number): BattleCalculations {
    const vehicles: VehicleCalculations[] = Object.values(battle.model.vehicles)
        .map((vehicle: VehicleModel) => new VehicleCalculations(vehicle))
    return new BattleCalculations(battle.model, vehicles, timeStepSecs)
  }

  return { process }
}
