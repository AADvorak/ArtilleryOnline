import type {CommandsSender} from "@/playground/composables/commands-sender";
import {Command, type UserCommand} from "@/playground/data/command";
import {MovingDirection} from "@/playground/data/common";

export function useColliderKeyboardListener(commandsSender: CommandsSender) {

  const keyDownCommands: Map<string, UserCommand> = new Map()
  keyDownCommands.set('KeyD', {
    command: Command.START_PUSHING,
    params: { direction: MovingDirection.RIGHT }
  })
  keyDownCommands.set('KeyA', {
    command: Command.START_PUSHING,
    params: { direction: MovingDirection.LEFT }
  })
  keyDownCommands.set('KeyW', {
    command: Command.START_PUSHING,
    params: { direction: MovingDirection.UP }
  })
  keyDownCommands.set('KeyS', {
    command: Command.START_PUSHING,
    params: { direction: MovingDirection.DOWN }
  })
  keyDownCommands.set('KeyQ', {
    command: Command.START_ROTATING,
    params: { direction: MovingDirection.LEFT }
  })
  keyDownCommands.set('KeyE', {
    command: Command.START_ROTATING,
    params: { direction: MovingDirection.RIGHT }
  })

  const keyUpCommands: Map<string, UserCommand> = new Map()
  keyUpCommands.set('KeyD', {
    command: Command.STOP_PUSHING,
    params: { direction: MovingDirection.RIGHT }
  })
  keyUpCommands.set('KeyA', {
    command: Command.STOP_PUSHING,
    params: { direction: MovingDirection.LEFT }
  })
  keyUpCommands.set('KeyW', {
    command: Command.STOP_PUSHING,
    params: { direction: MovingDirection.UP }
  })
  keyUpCommands.set('KeyS', {
    command: Command.STOP_PUSHING,
    params: { direction: MovingDirection.DOWN }
  })
  keyUpCommands.set('KeyQ', {
    command: Command.STOP_ROTATING,
    params: { direction: MovingDirection.LEFT }
  })
  keyUpCommands.set('KeyE', {
    command: Command.STOP_ROTATING,
    params: { direction: MovingDirection.RIGHT }
  })

  const keysDown: Map<string, string> = new Map()

  function startListening() {
    addEventListener('keyup', keyupListener)
    addEventListener('keydown', keydownListener)
  }

  function stopListening() {
    removeEventListener('keyup', keyupListener)
    removeEventListener('keydown', keydownListener)
  }

  function keyupListener(e: KeyboardEvent) {
    keysDown.delete(e.code)
    const command = keyUpCommands.get(e.code)
    if (command) {
      commandsSender.sendDebugCommand(command)
    }
  }

  function keydownListener(e: KeyboardEvent) {
    if (!keysDown.has(e.code)) {
      keysDown.set(e.code, e.key)
      const command = keyDownCommands.get(e.code)
      if (command) {
        commandsSender.sendDebugCommand(command)
      }
    }
  }

  return { startListening, stopListening }
}
