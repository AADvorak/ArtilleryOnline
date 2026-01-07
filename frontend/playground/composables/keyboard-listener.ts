import type {CommandsSender} from "@/playground/composables/commands-sender";
import {Command, type UserCommand} from "@/playground/data/command";
import {MovingDirection} from "@/playground/data/common";
import {useUserSettingsStore} from "~/stores/user-settings";

export function useKeyboardListener(commandsSender: CommandsSender) {
  const mapping = useUserSettingsStore().controlsOrDefaultsValueNameMapping

  const keyDownCommands: Map<string, UserCommand> = new Map()
  keyDownCommands.set('moveRight', {
    command: Command.START_MOVING,
    params: { direction: MovingDirection.RIGHT }
  })
  keyDownCommands.set('moveLeft', {
    command: Command.START_MOVING,
    params: { direction: MovingDirection.LEFT }
  })
  keyDownCommands.set('rotateGunRight', {
    command: Command.START_GUN_ROTATING,
    params: { direction: MovingDirection.RIGHT }
  })
  keyDownCommands.set('rotateGunLeft', {
    command: Command.START_GUN_ROTATING,
    params: { direction: MovingDirection.LEFT }
  })
  keyDownCommands.set('shoot', {
    command: Command.PUSH_TRIGGER
  })
  keyDownCommands.set('activateJet', {
    command: Command.JET_ON
  })

  const keyUpCommands: Map<string, UserCommand> = new Map()
  keyUpCommands.set('moveRight', {
    command: Command.STOP_MOVING,
    params: { direction: MovingDirection.RIGHT }
  })
  keyUpCommands.set('moveLeft', {
    command: Command.STOP_MOVING,
    params: { direction: MovingDirection.LEFT }
  })
  keyUpCommands.set('rotateGunRight', {
    command: Command.STOP_GUN_ROTATING,
    params: { direction: MovingDirection.RIGHT }
  })
  keyUpCommands.set('rotateGunLeft', {
    command: Command.STOP_GUN_ROTATING,
    params: { direction: MovingDirection.LEFT }
  })
  keyUpCommands.set('shoot', {
    command: Command.RELEASE_TRIGGER
  })
  keyUpCommands.set('activateJet', {
    command: Command.JET_OFF
  })

  const clickCommands: Map<string, UserCommand> = new Map()
  clickCommands.set('autoMoveRight', {
    command: Command.START_MOVING,
    params: { direction: MovingDirection.RIGHT }
  })
  clickCommands.set('autoMoveLeft', {
    command: Command.START_MOVING,
    params: { direction: MovingDirection.LEFT }
  })
  clickCommands.set('launchMissile', {
    command: Command.LAUNCH_MISSILE
  })
  clickCommands.set('launchDrone', {
    command: Command.LAUNCH_DRONE
  })
  clickCommands.set('switchGunMode', {
    command: Command.SWITCH_GUN_MODE
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
    const userCommandKey = mapping[e.code]
    if (userCommandKey) {
      e.preventDefault()
      const userCommand = keyUpCommands.get(userCommandKey)
          || clickCommands.get(userCommandKey)
      if (userCommand) {
        commandsSender.sendCommand(userCommand)
      }
    }
  }

  function keydownListener(e: KeyboardEvent) {
    if (!keysDown.has(e.code)) {
      e.preventDefault()
      keysDown.set(e.code, e.key)
      const userCommandKey = mapping[e.code]
      if (userCommandKey) {
        const userCommand = keyDownCommands.get(userCommandKey)
        if (userCommand) {
          commandsSender.sendCommand(userCommand)
        }
      }
    }
  }

  return { startListening, stopListening }
}
