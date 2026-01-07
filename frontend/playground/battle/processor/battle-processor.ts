import {useBattleStore} from "~/stores/battle";
import type {Battle} from "@/playground/data/battle";
import {BattleStage} from "@/playground/data/battle";
import {ParticleProcessor} from "~/playground/battle/processor/particle-processor";
import type {BodyParticleModel, ParticleModel, ShellModel} from "~/playground/data/model";
import {DefaultColors} from "~/dictionary/default-colors";
import {BattleUtils} from "~/playground/utils/battle-utils";
import {useBattleObjectsProcessor} from "~/playground/battle/processor/battle-objects-processor";
import {useBattleSmoothTransition} from "~/playground/battle/processor/battle-smooth-transition";
import {Constants} from "~/playground/data/constants";
import {BodyParticleProcessor} from "~/playground/battle/processor/body-particle-processor";

export function useBattleProcessor() {

  const processing = ref(true)

  const battleStore = useBattleStore()

  const settingsStore = useSettingsStore()

  const smoothTransition = useBattleSmoothTransition()

  const battleObjectsProcessor = useBattleObjectsProcessor(
      settingsStore.settings?.debug || false,
      settingsStore.settings?.clientCollisionsProcessing || false,
      settingsStore.settings?.additionalResolveCollisionsIterationsNumber || 0
  )

  function startProcessing() {
    setTimeout(processStep, Constants.TIME_STEP_MS)
  }

  function stopProcessing() {
    processing.value = false
  }

  function processStep() {
    if (!battleStore.battle || !processing.value) {
      return
    }
    const battle = structuredClone(toRaw(battleStore.serverBattle!))
    if (battleStore.paused && !battleStore.doStep) {
      setTimeout(processStep, Constants.TIME_STEP_MS)
      return
    }
    if (battleStore.doStep) {
      battleStore.doStep = false
    }
    const battleStage = battle.battleStage
    if ([BattleStage.WAITING, BattleStage.ACTIVE].includes(battleStage)) {
      const currentTime = new Date().getTime()
      let timeStep = currentTime - battleStore.updateTime!
      if (timeStep > 10 * Constants.TIME_STEP_MS) {
        timeStep = Constants.TIME_STEP_MS + Math.ceil(Math.random() * 5)
      }
      const timeStepSecs = timeStep / 1000
      battle.time += timeStep
      if (BattleStage.ACTIVE === battleStage) {
        battleObjectsProcessor.process(battle, timeStepSecs)
        processStepActiveParticles(battle, timeStepSecs)
      }
      if (battleStore.needSmoothTransition) {
        battleStore.updateServerBattle(battle, currentTime)
        smoothTransition.process(battle, timeStepSecs)
      } else {
        battleStore.updateBattle(battle, currentTime)
      }
      setTimeout(processStep, Constants.TIME_STEP_MS)
    }
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
    Object.values(battleStore.bodyParticles).forEach((particle: BodyParticleModel) => {
      if (particle.state.remainTime <= 0) {
        battleStore.removeBodyParticle(particle.id)
      }
      BodyParticleProcessor.processStep(particle, timeStepSecs, battle.model.room.specs)
    })
  }

  return { startProcessing, stopProcessing }
}
