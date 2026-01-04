import type {Battle} from "~/playground/data/battle";
import {VehicleProcessor} from "~/playground/battle/processor/vehicle-processor";
import {ShellProcessor} from "~/playground/battle/processor/shell-processor";
import {ExplosionProcessor} from "~/playground/battle/processor/explosion-processor";
import {MissileProcessor} from "~/playground/battle/processor/missile-processor";
import {DroneProcessor} from "~/playground/battle/processor/drone-processor";
import {BoxProcessor} from "~/playground/battle/processor/box-processor";
import {BattleCalculations, BoxCalculations, VehicleCalculations} from "~/playground/data/calculations";
import type {BoxModel, VehicleModel} from "~/playground/data/model";
import {CollisionsProcessor} from "~/playground/battle/collision/collisions-processor";
import {
  VehicleGroundCollisionsDetector
} from "~/playground/battle/collision/detector/vehicle-ground-collision-detector";
import {BoxGroundCollisionsDetector} from "~/playground/battle/collision/detector/box-ground-collision-detector";
import {
  VehicleSurfaceCollisionsDetector
} from "~/playground/battle/collision/detector/vehicle-surface-collision-detector";
import {
  VehicleVehicleCollisionsDetector
} from "~/playground/battle/collision/detector/vehicle-vehicle-collision-detector";
import {BoxBoxCollisionsDetector} from "~/playground/battle/collision/detector/box-box-collisions-detector";
import {BoxVehicleCollisionsDetector} from "~/playground/battle/collision/detector/box-vehicle-collisions-detector";
import {BoxSurfaceCollisionsDetector} from "~/playground/battle/collision/detector/box-surface-collisions-detector";
import {useTrajectoryAndTargetProcessor} from "~/playground/battle/processor/trajectory-and-target-processor";

export const useBattleObjectsProcessor = function (
    debug: boolean,
    processCollisions: boolean,
    additionalIterationsNumber: number
){

  const trajectoryAndTargetProcessor = useTrajectoryAndTargetProcessor()

  const collisionsProcessor = new CollisionsProcessor(
      debug, additionalIterationsNumber,
      [
        new VehicleGroundCollisionsDetector(),
        new VehicleSurfaceCollisionsDetector(),
        new VehicleVehicleCollisionsDetector(),
        new BoxGroundCollisionsDetector(),
        new BoxSurfaceCollisionsDetector(),
        new BoxBoxCollisionsDetector(),
        new BoxVehicleCollisionsDetector()
      ],
      [], []
  )

  function process(battle: Battle, timeStepSecs: number) {
    const battleCalculations = initBattleCalculations(battle, timeStepSecs)
    trajectoryAndTargetProcessor.process(battleCalculations)
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
    battleCalculations.boxes.forEach(box => {
      BoxProcessor.processBeforeCollision(box, battleCalculations)
    })
    processCollisions && collisionsProcessor.process(battleCalculations)
    battleCalculations.vehicles.forEach(vehicle => {
      VehicleProcessor.processAfterCollision(vehicle, battleCalculations)
    })
    battleCalculations.boxes.forEach(box => {
      BoxProcessor.processAfterCollision(box, battleCalculations)
    })
  }

  function initBattleCalculations(battle: Battle, timeStepSecs: number): BattleCalculations {
    const vehicles: VehicleCalculations[] = Object.values(battle.model.vehicles)
        .map((vehicle: VehicleModel) => new VehicleCalculations(vehicle))
    const boxes: BoxCalculations[] = Object.values(battle.model.boxes)
        .map((box: BoxModel) => new BoxCalculations(box))
    return new BattleCalculations(battle.model, vehicles, boxes, timeStepSecs)
  }

  return { process }
}
