import { useBattleStore } from '~/stores/battle'
import type {Battle, BattleState} from '@/playground/data/battle'
import {useSettingsStore} from "~/stores/settings";
import {useStompClientStore} from "~/stores/stomp-client";

export function useBattleUpdater() {
  const battleStore = useBattleStore()
  const settingsStore = useSettingsStore()
  const stompClientStore = useStompClientStore()

  function subscribe() {
    const battle = battleStore.battle as Battle
    stompClientStore.client?.subscribe('/topic/battle/updates/' + battle.id, function (msgOut) {
      const battle = JSON.parse(msgOut.body) as Battle
      if (battle) {
        updateBattle(battle)
      }
    })
    stompClientStore.client?.subscribe('/topic/battle/updates/' + battle.id + '/state', function (msgOut) {
      const battleState = JSON.parse(msgOut.body) as BattleState
      if (battleState) {
        updateBattleState(battleState)
      }
    })
  }

  function updateBattle(battle: Battle) {
    !battle.paused || battle.model.updated || !settingsStore.settings!.clientProcessing
        ? battleStore.updateBattle(battle) : battleStore.updateServerBattle(battle)
  }

  function updateBattleState(battleState: BattleState) {
    const battle = JSON.parse(JSON.stringify(battleStore.battle)) as Battle
    battle.time = battleState.time
    Object.keys(battleState.state.vehicles).forEach(key => {
      battle.model.vehicles[key].state = battleState.state.vehicles[key]
    })
    Object.keys(battleState.state.shells).forEach(key => {
      battle.model.shells[key].state = battleState.state.shells[key]
    })
    battleStore.updateBattle(battle)
  }

  return { subscribe }
}
