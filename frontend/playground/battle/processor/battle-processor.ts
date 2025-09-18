import {useBattleStore} from "~/stores/battle";
import {BattleStage} from "@/playground/data/battle";
import type {Battle} from "@/playground/data/battle";
import {VehicleProcessor} from "@/playground/battle/processor/vehicle-processor";
import {ShellProcessor} from "@/playground/battle/processor/shell-processor";
import {ExplosionProcessor} from "@/playground/battle/processor/explosion-processor";
import {MissileProcessor} from "~/playground/battle/processor/missile-processor";
import {DroneProcessor} from "~/playground/battle/processor/drone-processor";
import {ParticleProcessor} from "~/playground/battle/processor/particle-processor";
import type {ParticleModel, ShellModel} from "~/playground/data/model";
import type {Position} from "~/playground/data/common";
import type {ParticleState} from "~/playground/data/state";
import {DefaultColors} from "~/dictionary/default-colors";
import {BoxProcessor} from "~/playground/battle/processor/box-processor";

export function useBattleProcessor() {

  const TIME_STEP_MS = 10

  const processing = ref(true)

  const battleStore = useBattleStore()

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
    Object.values(battle.model.vehicles).forEach(vehicle => {
      VehicleProcessor.processStep(vehicle, battle.model, timeStepSecs)
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
  }

  function processStepActiveParticles(battle: Battle, timeStepSecs: number) {
    Object.values(battle.model.shells).forEach((shell: ShellModel) => {
      if (shell.state.stuck) {
        battleStore.addParticle(generateParticle(shell.state.position), {color: DefaultColors.SIGNAL_SHELL})
      }
    })
    Object.values(battleStore.particles).forEach((particle: ParticleModel) => {
      if (particle.state.remainTime <= 0) {
        battleStore.removeParticle(particle.id)
      }
      ParticleProcessor.processStep(particle, timeStepSecs, battle.model.room.specs)
    })
  }

  function generateParticle(position: Position): ParticleState {
    const velocityMagnitude = 2 + 0.5 * Math.random()
    const velocityAngle = Math.PI / 4 + Math.PI * Math.random() / 2
    const remainTime = 0.3 * Math.random()
    const {x, y} = position
    return {
      position: {x, y},
      velocity: {
        x: velocityMagnitude * Math.cos(velocityAngle),
        y: velocityMagnitude * Math.sin(velocityAngle),
      },
      remainTime
    }
  }

  return { startProcessing, stopProcessing }
}
