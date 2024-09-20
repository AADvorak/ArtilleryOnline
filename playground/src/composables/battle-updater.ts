import { useBattleStore } from '@/stores/battle'
import type { Battle } from '@/data/battle'
import type {StompClient} from "@/composables/stomp-client";

export function useBattleUpdater(stompClient: StompClient) {
  const battleStore = useBattleStore()

  function subscribeAfterWsConnect() {
    stompClient.addConnectCallback(() => {
      const battle = battleStore.battle as Battle
      stompClient.client.value?.subscribe('/topic/battle/updates/' + battle.id, function (msgOut) {
        const battle = JSON.parse(msgOut.body) as Battle
        if (battle) {
          battleStore.battle = battle
        }
      })
    })
  }

  return { subscribeAfterWsConnect }
}
