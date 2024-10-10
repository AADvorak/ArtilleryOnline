import type {DebugCommand, UserCommand} from '@/playground/data/command'
import { useUserKeyStore } from '@/playground/stores/user-key'
import { useBattleStore } from '@/playground/stores/battle'
import type {StompClient} from "@/playground/composables/stomp-client";

export interface CommandsSender {
  sendCommand: Function
  sendDebugCommand: Function
}

export function useCommandsSender(stompClient: StompClient) {
  const userStore = useUserKeyStore()

  const battleStore = useBattleStore()

  function sendCommand(userCommand: UserCommand) {
    if (userStore.userKey && battleStore.isActive) {
      stompClient.client.value?.send(
        '/api/ws/battle/commands',
        {},
        JSON.stringify({ userKey: userStore.userKey, userCommand })
      )
    }
  }

  function sendDebugCommand(debugCommand: DebugCommand) {
    if (userStore.userKey && battleStore.battle) {
      stompClient.client.value?.send(
          '/api/ws/battle/debug-commands',
          {},
          JSON.stringify({ userKey: userStore.userKey, debugCommand })
      )
    }
  }

  return { sendCommand, sendDebugCommand }
}
