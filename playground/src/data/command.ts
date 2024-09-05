import type {MovingDirection} from "@/data/common";

export enum Command {
  START_MOVING = 'START_MOVING',
  STOP_MOVING = 'STOP_MOVING',
  PUSH_TRIGGER = 'PUSH_TRIGGER',
  RELEASE_TRIGGER = 'RELEASE_TRIGGER',
  START_GUN_ROTATING = 'START_GUN_ROTATING',
  STOP_GUN_ROTATING = 'STOP_GUN_ROTATING'
}

export interface CommandParams {
  direction: MovingDirection
}

export interface UserCommand {
  command: Command
  params?: CommandParams
}
