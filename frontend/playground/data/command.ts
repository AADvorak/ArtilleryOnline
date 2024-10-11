import {type MovingDirection, ShellType} from "@/playground/data/common";

export enum Command {
  START_MOVING = 'START_MOVING',
  STOP_MOVING = 'STOP_MOVING',
  PUSH_TRIGGER = 'PUSH_TRIGGER',
  RELEASE_TRIGGER = 'RELEASE_TRIGGER',
  START_GUN_ROTATING = 'START_GUN_ROTATING',
  STOP_GUN_ROTATING = 'STOP_GUN_ROTATING',
  SELECT_SHELL = 'SELECT_SHELL',

  PAUSE = 'PAUSE',
  RESUME = 'RESUME',
  STEP = 'STEP',
  START_TRACKING = 'START_TRACKING',
  STOP_TRACKING = 'STOP_TRACKING'
}

export interface CommandParams {
  direction?: MovingDirection
  shellType?: ShellType
}

export interface UserCommand {
  command: Command
  params?: CommandParams
}

export interface DebugCommand {
  command: Command
}
