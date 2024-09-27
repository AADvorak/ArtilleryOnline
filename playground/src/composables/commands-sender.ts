import type {DebugCommand, UserCommand} from '@/data/command'
import { Command } from '@/data/command'
import { MovingDirection } from '@/data/common'
import { useUserStore } from '@/stores/user'
import { useBattleStore } from '@/stores/battle'
import type {StompClient} from "@/composables/stomp-client";

export function useCommandsSender(stompClient: StompClient) {
  const keyDownCommands: Map<string, UserCommand> = new Map()
  keyDownCommands.set('KeyD', {
    command: Command.START_MOVING,
    params: { direction: MovingDirection.RIGHT }
  })
  keyDownCommands.set('KeyA', {
    command: Command.START_MOVING,
    params: { direction: MovingDirection.LEFT }
  })
  keyDownCommands.set('ArrowRight', {
    command: Command.START_GUN_ROTATING,
    params: { direction: MovingDirection.RIGHT }
  })
  keyDownCommands.set('ArrowLeft', {
    command: Command.START_GUN_ROTATING,
    params: { direction: MovingDirection.LEFT }
  })
  keyDownCommands.set('Space', {
    command: Command.PUSH_TRIGGER
  })

  const keyUpCommands: Map<string, UserCommand> = new Map()
  keyUpCommands.set('KeyD', {
    command: Command.STOP_MOVING,
    params: { direction: MovingDirection.RIGHT }
  })
  keyUpCommands.set('KeyA', {
    command: Command.STOP_MOVING,
    params: { direction: MovingDirection.LEFT }
  })
  keyUpCommands.set('ArrowRight', {
    command: Command.STOP_GUN_ROTATING,
    params: { direction: MovingDirection.RIGHT }
  })
  keyUpCommands.set('ArrowLeft', {
    command: Command.STOP_GUN_ROTATING,
    params: { direction: MovingDirection.LEFT }
  })
  keyUpCommands.set('Space', {
    command: Command.RELEASE_TRIGGER
  })

  const keysDown: Map<string, string> = new Map()

  const userStore = useUserStore()

  const battleStore = useBattleStore()

  function startSending() {
    document.addEventListener('keyup', (e) => {
      keysDown.delete(e.code)
      const userCommand = keyUpCommands.get(e.code)
      if (userCommand) {
        sendCommand(userCommand)
      }
    })
    document.addEventListener('keydown', (e) => {
      if (!keysDown.has(e.code)) {
        keysDown.set(e.code, e.key)
        const userCommand = keyDownCommands.get(e.code)
        if (userCommand) {
          sendCommand(userCommand)
        }
      }
    })
  }

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

  return { startSending, sendCommand, sendDebugCommand }
}
