import {ref} from "vue";
import {Stomp} from "@stomp/stompjs";
import {useBattleStore} from "@/stores/battle";
import type {Battle} from "@/data/battle";

export function useBattleUpdater() {

  const battleStore = useBattleStore()

  const socket = ref()
  const stompClient = ref()

  function startListening() {
    const battle = battleStore.battle as Battle
    socket.value = new WebSocket('ws://localhost:8080/battle-updates/websocket');
    stompClient.value = Stomp.over(socket.value)

    stompClient.value.connect({}, function () {
      stompClient.value.subscribe('/topic/battle-updates/' + battle.id, function (msgOut) {
        const battle = JSON.parse(msgOut.body) as Battle
        if (battle) {
          battleStore.battle = battle
        }
      })
    })
  }

  return { startListening }
}
