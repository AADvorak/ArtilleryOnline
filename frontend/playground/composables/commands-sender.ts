import type {DebugCommand, UserCommand} from '@/playground/data/command'
import { useBattleStore } from '~/stores/battle'
import {useStompClientStore} from "~/stores/stomp-client";

export interface CommandsSender {
  sendCommand: Function
  sendDebugCommand: Function
}

export function useCommandsSender() {
  const battleStore = useBattleStore()
  const stompClientStore = useStompClientStore()

  function sendCommand(userCommand: UserCommand) {
    if (battleStore.isActive) {
      stompClientStore.client?.send(
        '/api/ws/battle/commands',
        {},
        JSON.stringify(userCommand)
      )
    }
  }

  function sendDebugCommand(debugCommand: DebugCommand) {
    if (battleStore.battle) {
      stompClientStore.client?.send(
          '/api/ws/battle/debug-commands',
          {},
          JSON.stringify(debugCommand)
      )
    }
  }

  return { sendCommand, sendDebugCommand }
}
