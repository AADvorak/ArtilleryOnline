import type {DebugCommand, UserCommand} from '@/playground/data/command'
import { useBattleStore } from '~/stores/battle'
import type {StompClient} from "@/playground/composables/stomp-client";

export interface CommandsSender {
  sendCommand: Function
  sendDebugCommand: Function
}

export function useCommandsSender(stompClient: StompClient) {
  const battleStore = useBattleStore()

  function sendCommand(userCommand: UserCommand) {
    if (battleStore.isActive) {
      stompClient.client.value?.send(
        '/api/ws/battle/commands',
        {},
        JSON.stringify(userCommand)
      )
    }
  }

  function sendDebugCommand(debugCommand: DebugCommand) {
    if (battleStore.battle) {
      stompClient.client.value?.send(
          '/api/ws/battle/debug-commands',
          {},
          JSON.stringify(debugCommand)
      )
    }
  }

  return { sendCommand, sendDebugCommand }
}
