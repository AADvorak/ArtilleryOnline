import type {CommandsSender} from "@/playground/composables/commands-sender";
import {Command} from "~/playground/data/command";
import {MovingDirection} from "~/playground/data/common";

enum TouchPosition {
  RIGHT_UP,
  LEFT_UP,
  RIGHT_DOWN,
  LEFT_DOWN,
  CENTER_UP,
  CENTER_DOWN
}

enum TouchType {
  START = 'touchstart',
  END = 'touchend'
}

export function useMobileDeviceListener(commandsSender: CommandsSender) {

  function startListening() {
    addEventListener(TouchType.START, handleTouchStart, false)
    addEventListener(TouchType.END, handleTouchEnd, false)
  }

  function stopListening() {
    removeEventListener(TouchType.START, handleTouchStart)
    removeEventListener(TouchType.END, handleTouchEnd)
  }

  function handleTouchStart(event: TouchEvent) {
    // @ts-ignore
    const touchPosition = getTouchPosition(event)
    const command = getCommand(TouchType.START, touchPosition)
    commandsSender.sendCommand(command)
  }

  function handleTouchEnd(event: TouchEvent) {
    // @ts-ignore
    const touchPosition = getTouchPosition(event)
    const command = getCommand(TouchType.END, touchPosition)
    commandsSender.sendCommand(command)
  }

  function getTouchPosition(event: Touch) {
    const height = window.innerHeight
    const width = window.innerWidth
    if (event.pageY < height / 2) {
      if (event.pageX < width / 3) {
        return TouchPosition.LEFT_UP
      } else if (event.pageX < 2 * width / 3) {
        return TouchPosition.CENTER_UP
      } else {
        return TouchPosition.RIGHT_UP
      }
    } else {
      if (event.pageX < width / 3) {
        return TouchPosition.LEFT_DOWN
      } else if (event.pageX < 2 * width / 3) {
        return TouchPosition.CENTER_DOWN
      } else {
        return TouchPosition.RIGHT_DOWN
      }
    }
  }

  function getCommand(touchType: TouchType, touchPosition: TouchPosition) {
    if (touchType === TouchType.START) {
      if (touchPosition === TouchPosition.RIGHT_DOWN) {
        return {
          command: Command.START_MOVING,
          params: { direction: MovingDirection.RIGHT }
        }
      }
      if (touchPosition === TouchPosition.LEFT_DOWN) {
        return {
          command: Command.START_MOVING,
          params: { direction: MovingDirection.LEFT }
        }
      }
      if (touchPosition === TouchPosition.RIGHT_UP) {
        return {
          command: Command.START_GUN_ROTATING,
          params: { direction: MovingDirection.RIGHT }
        }
      }
      if (touchPosition === TouchPosition.LEFT_UP) {
        return {
          command: Command.START_GUN_ROTATING,
          params: { direction: MovingDirection.LEFT }
        }
      }
      if (touchPosition === TouchPosition.CENTER_DOWN) {
        return {
          command: Command.PUSH_TRIGGER
        }
      }
      if (touchPosition === TouchPosition.CENTER_UP) {
        return {
          command: Command.JET_ON
        }
      }
    }
    if (touchType === TouchType.END) {
      if (touchPosition === TouchPosition.RIGHT_DOWN) {
        return {
          command: Command.STOP_MOVING,
          params: { direction: MovingDirection.RIGHT }
        }
      }
      if (touchPosition === TouchPosition.LEFT_DOWN) {
        return {
          command: Command.STOP_MOVING,
          params: { direction: MovingDirection.LEFT }
        }
      }
      if (touchPosition === TouchPosition.RIGHT_UP) {
        return {
          command: Command.STOP_GUN_ROTATING,
          params: { direction: MovingDirection.RIGHT }
        }
      }
      if (touchPosition === TouchPosition.LEFT_UP) {
        return {
          command: Command.STOP_GUN_ROTATING,
          params: { direction: MovingDirection.LEFT }
        }
      }
      if (touchPosition === TouchPosition.CENTER_DOWN) {
        return {
          command: Command.RELEASE_TRIGGER
        }
      }
      if (touchPosition === TouchPosition.CENTER_UP) {
        return {
          command: Command.JET_OFF
        }
      }
    }
  }

  return { startListening, stopListening }
}
