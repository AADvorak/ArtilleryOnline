import type { UserCommand } from '@/data/command'
import { Command } from '@/data/command'
import { MovingDirection } from '@/data/common'
import {ApiRequestSender} from "@/api/api-request-sender";
import {useUserStore} from "@/stores/user";
import {useBattleStore} from "@/stores/battle";

export function useCommandsSender() {

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
    command: Command.STOP_MOVING
  })
  keyUpCommands.set('KeyA', {
    command: Command.STOP_MOVING
  })
  keyUpCommands.set('ArrowRight', {
    command: Command.STOP_GUN_ROTATING
  })
  keyUpCommands.set('ArrowLeft', {
    command: Command.STOP_GUN_ROTATING
  })
  keyUpCommands.set('Space', {
    command: Command.RELEASE_TRIGGER
  })

  const keysDown: Map<string, string> = new Map()

  const apiRequestSender = new ApiRequestSender()

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
      apiRequestSender.postJson('/battles/commands', userStore.userKey, userCommand).then()
    }
  }

  return { startSending }
}
