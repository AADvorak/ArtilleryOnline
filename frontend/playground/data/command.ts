import {type MovingDirection} from "@/playground/data/common";

export enum Command {
  START_MOVING = 'START_MOVING',
  STOP_MOVING = 'STOP_MOVING',
  PUSH_TRIGGER = 'PUSH_TRIGGER',
  RELEASE_TRIGGER = 'RELEASE_TRIGGER',
  START_GUN_ROTATING = 'START_GUN_ROTATING',
  STOP_GUN_ROTATING = 'STOP_GUN_ROTATING',
  SELECT_SHELL = 'SELECT_SHELL',
  JET_ON = 'JET_ON',
  JET_OFF = 'JET_OFF',
  LAUNCH_MISSILE = 'LAUNCH_MISSILE',
  LAUNCH_DRONE = 'LAUNCH_DRONE',
  SWITCH_GUN_MODE = 'SWITCH_GUN_MODE',

  PAUSE = 'PAUSE',
  RESUME = 'RESUME',
  STEP = 'STEP',
  START_TRACKING = 'START_TRACKING',
  STOP_TRACKING = 'STOP_TRACKING',
  START_ROTATING = 'START_ROTATING',
  STOP_ROTATING = 'STOP_ROTATING',
  START_PUSHING = 'START_PUSHING',
  STOP_PUSHING = 'STOP_PUSHING',
}

export interface CommandParams {
  direction?: MovingDirection
  shellName?: string
}

export interface UserCommand {
  command: Command
  params?: CommandParams
}

export interface DebugCommand {
  command: Command
  params?: CommandParams
}
