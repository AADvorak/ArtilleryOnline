import type {BattleModel} from "@/playground/data/model";
import type {BattleModelState} from "@/playground/data/state";
import type {BattleModelUpdates} from "~/playground/data/updates";
import type {BattleModelEvents} from "~/playground/data/events";

export enum BattleStage {
  WAITING = 'WAITING',
  ACTIVE = 'ACTIVE',
  FINISHED = 'FINISHED',
  ERROR = 'ERROR'
}

export enum BattleType {
  TEST_DRIVE = 'TEST_DRIVE',
  RANDOM = 'RANDOM',
  ROOM = 'ROOM',
  DRONE_HUNT = 'DRONE_HUNT',
  COLLIDER = 'COLLIDER'
}

export interface Battle {
  id: string
  model: BattleModel
  time: number
  fps: number
  paused: boolean
  battleStage: BattleStage
  type: BattleType
}

export interface BattleUpdate {
  time: number
  fps: number
  stage?: BattleStage
  state?: BattleModelState
  updates?: BattleModelUpdates
  events?: BattleModelEvents
}
