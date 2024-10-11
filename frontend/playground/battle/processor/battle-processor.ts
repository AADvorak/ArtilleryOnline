import {useBattleStore} from "~/stores/battle";
import {BattleStage} from "@/playground/data/battle";
import type {Battle} from "@/playground/data/battle";
import {VehicleProcessor} from "@/playground/battle/processor/vehicle-processor";
import {ShellProcessor} from "@/playground/battle/processor/shell-processor";
import {ExplosionProcessor} from "@/playground/battle/processor/explosion-processor";

export function useBattleProcessor() {

  const TIME_STEP_MS = 15

  const battleStore = useBattleStore()

  function startProcessing() {
    setTimeout(processStep, TIME_STEP_MS)
  }

  function processStep() {
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
        timeStep = TIME_STEP_MS
      }
      const timeStepSecs = timeStep / 1000
      battle.time += timeStep
      if (BattleStage.ACTIVE === battleStage) {
        processStepActive(battle, timeStepSecs)
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
      ShellProcessor.processStep(shell, timeStepSecs, battle.model.room.specs.gravityAcceleration)
    })
    Object.values(battle.model.explosions).forEach(explosion => {
      ExplosionProcessor.processStep(explosion, timeStepSecs)
    })
  }

  return { startProcessing }
}
