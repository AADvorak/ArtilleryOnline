import { useBattleStore } from '@/playground/stores/battle'
import type {Battle, BattleState} from '@/playground/data/battle'
import type {StompClient} from "@/playground/composables/stomp-client";
import {useSettingsStore} from "@/playground/stores/settings";

export function useBattleUpdater(stompClient: StompClient) {
  const battleStore = useBattleStore()
  const settingsStore = useSettingsStore()

  function subscribeAfterWsConnect() {
    stompClient.addConnectCallback(() => {
      const battle = battleStore.battle as Battle
      stompClient.client.value?.subscribe('/topic/battle/updates/' + battle.id, function (msgOut) {
        const battle = JSON.parse(msgOut.body) as Battle
        if (battle) {
          updateBattle(battle)
        }
      })
      stompClient.client.value?.subscribe('/topic/battle/updates/' + battle.id + '/state', function (msgOut) {
        const battleState = JSON.parse(msgOut.body) as BattleState
        if (battleState) {
          updateBattleState(battleState)
        }
      })
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
    battleStore.updateBattle(battle)
  }

  return { subscribeAfterWsConnect }
}
