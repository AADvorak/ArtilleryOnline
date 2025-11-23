import {useBattleStore} from "~/stores/battle";
import type {Battle} from "@/playground/data/battle";
import {BattleStage} from "@/playground/data/battle";
import {VehicleProcessor} from "@/playground/battle/processor/vehicle-processor";
import {ShellProcessor} from "@/playground/battle/processor/shell-processor";
import {ExplosionProcessor} from "@/playground/battle/processor/explosion-processor";
import {MissileProcessor} from "~/playground/battle/processor/missile-processor";
import {DroneProcessor} from "~/playground/battle/processor/drone-processor";
import {ParticleProcessor} from "~/playground/battle/processor/particle-processor";
import type {ParticleModel, ShellModel, VehicleModel} from "~/playground/data/model";
import {DefaultColors} from "~/dictionary/default-colors";
import {BoxProcessor} from "~/playground/battle/processor/box-processor";
import {BattleUtils} from "~/playground/utils/battle-utils";
import {BattleCalculations, VehicleCalculations} from "~/playground/data/calculations";
import {CollisionsProcessor} from "~/playground/battle/collision/collisions-processor";
import {
  VehicleGroundCollisionsDetector
} from "~/playground/battle/collision/detector/vehicle-ground-collision-detector";

export function useBattleProcessor() {

  const TIME_STEP_MS = 10

  const processing = ref(true)

  const battleStore = useBattleStore()

  const settingsStore = useSettingsStore()

  const collisionsProcessor = new CollisionsProcessor(
      settingsStore.settings?.debug || false,
      settingsStore.settings?.additionalResolveCollisionsIterationsNumber || 0,
      [new VehicleGroundCollisionsDetector()], [], []
  )

  function startProcessing() {
    setTimeout(processStep, TIME_STEP_MS)
  }

  function stopProcessing() {
    processing.value = false
  }

  function processStep() {
    if (!battleStore.battle || !processing.value) {
      return
    }
    const battle = JSON.parse(JSON.stringify(battleStore.battle)) as Battle
    if (battleStore.paused && !battleStore.doStep) {
      setTimeout(processStep, TIME_STEP_MS)
      return
    }
    if (battleStore.doStep) {
      battleStore.doStep = false
    }
    const battleStage = battle.battleStage
    if ([BattleStage.WAITING, BattleStage.ACTIVE].includes(battleStage)) {
      const currentTime = new Date().getTime()
      let timeStep = currentTime - battleStore.updateTime!
      if (timeStep > 10 * TIME_STEP_MS) {
        timeStep = TIME_STEP_MS + Math.ceil(Math.random() * 5)
      }
      const timeStepSecs = timeStep / 1000
      battle.time += timeStep
      if (BattleStage.ACTIVE === battleStage) {
        processStepActive(battle, timeStepSecs)
        processStepActiveParticles(battle, timeStepSecs)
      }
      battleStore.updateClientBattle(battle, currentTime)
      setTimeout(processStep, TIME_STEP_MS)
    }
  }

  function processStepActive(battle: Battle, timeStepSecs: number) {
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
    collisionsProcessor.process(battleCalculations)
    battleCalculations.vehicles.forEach(vehicle => {
      VehicleProcessor.processAfterCollision(vehicle, battleCalculations)
    })
  }

  function processStepActiveParticles(battle: Battle, timeStepSecs: number) {
    Object.values(battle.model.shells).forEach((shell: ShellModel) => {
      if (shell.state.stuck) {
        battleStore.addParticle(BattleUtils.generateParticle(shell.state.position), {color: DefaultColors.SIGNAL_SHELL})
      }
    })
    Object.values(battleStore.particles).forEach((particle: ParticleModel) => {
      if (particle.state.remainTime <= 0) {
        battleStore.removeParticle(particle.id)
      }
      ParticleProcessor.processStep(particle, timeStepSecs, battle.model.room.specs)
    })
  }

  function initBattleCalculations(battle: Battle, timeStepSecs: number): BattleCalculations {
    const vehicles: VehicleCalculations[] = Object.values(battle.model.vehicles)
        .map((vehicle: VehicleModel) => new VehicleCalculations(vehicle))
    return new BattleCalculations(battle.model, vehicles, timeStepSecs)
  }

  return { startProcessing, stopProcessing }
}
