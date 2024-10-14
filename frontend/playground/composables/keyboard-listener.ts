import type {CommandsSender} from "@/playground/composables/commands-sender";
import {Command, type UserCommand} from "@/playground/data/command";
import {MovingDirection} from "@/playground/data/common";

export function useKeyboardListener(commandsSender: CommandsSender) {
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
  keyDownCommands.set('KeyW', {
    command: Command.JET_ON
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
  keyUpCommands.set('KeyW', {
    command: Command.JET_OFF
  })

  const clickCommands: Map<string, UserCommand> = new Map()
  clickCommands.set('KeyE', {
    command: Command.START_MOVING,
    params: { direction: MovingDirection.RIGHT }
  })
  clickCommands.set('KeyQ', {
    command: Command.START_MOVING,
    params: { direction: MovingDirection.LEFT }
  })

  const keysDown: Map<string, string> = new Map()

  function startListening() {
    document.addEventListener('keyup', (e) => {
      keysDown.delete(e.code)
      const userCommand = keyUpCommands.get(e.code) || clickCommands.get(e.code)
      if (userCommand) {
        commandsSender.sendCommand(userCommand)
      }
    })
    document.addEventListener('keydown', (e) => {
      if (!keysDown.has(e.code)) {
        keysDown.set(e.code, e.key)
        const userCommand = keyDownCommands.get(e.code)
        if (userCommand) {
          commandsSender.sendCommand(userCommand)
        }
      }
    })
  }

  return { startListening }
}
