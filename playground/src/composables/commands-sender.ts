import type {DebugCommand, UserCommand} from '@/data/command'
import { useUserStore } from '@/stores/user'
import { useBattleStore } from '@/stores/battle'
import type {StompClient} from "@/composables/stomp-client";

export interface CommandsSender {
  sendCommand: Function
  sendDebugCommand: Function
}

export function useCommandsSender(stompClient: StompClient) {
  const userStore = useUserStore()

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
    if (userStore.userKey && battleStore.isActive) {
      stompClient.client.value?.send(
          '/api/ws/battle/debug-commands',
          {},
          JSON.stringify({ userKey: userStore.userKey, debugCommand })
      )
    }
  }

  return { sendCommand, sendDebugCommand }
}
