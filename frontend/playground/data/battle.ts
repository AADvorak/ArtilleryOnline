import type {BattleModel} from "@/playground/data/model";
import type {BattleModelState} from "@/playground/data/state";
import type {BattleModelUpdates} from "~/playground/data/updates";
import type {BattleModelEvents} from "~/playground/data/events";

export enum BattleStage {
  WAITING = 'WAITING',
  ACTIVE = 'ACTIVE',
  FINISHED = 'FINISHED'
}

export interface Battle {
  id: string
  model: BattleModel
  time: number
  paused: boolean
  battleStage: BattleStage
}

export interface BattleUpdate {
  id: string
  time: number
  state?: BattleModelState
  updates?: BattleModelUpdates
  events?: BattleModelEvents
}
