import {useBattleStore} from "@/stores/battle";
import {BattleStage} from "@/data/battle";
import type {Battle} from "@/data/battle";
import {VehicleProcessor} from "@/battle/processor/vehicle-processor";
import {ShellProcessor} from "@/battle/processor/shell-processor";
import {ExplosionProcessor} from "@/battle/processor/explosion-processor";

export function useBattleProcessor() {

  const TIME_STEP_MS = 15

  const battleStore = useBattleStore()

  function startProcessing() {
    setTimeout(processStep, TIME_STEP_MS)
  }

  function processStep() {
    const battle = JSON.parse(JSON.stringify(battleStore.battle)) as Battle
    const battleStage = battle.battleStage
    if ([BattleStage.WAITING, BattleStage.ACTIVE].includes(battleStage)) {
      const currentTime = new Date().getTime()
      const timeStep = currentTime - battleStore.updateTime!
      const timeStepSecs = timeStep / 1000
      battle.time += timeStep
      if (BattleStage.ACTIVE === battleStage) {
        processStepActive(battle, timeStepSecs)
      }
      battleStore.updateBattle(battle, currentTime)
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
