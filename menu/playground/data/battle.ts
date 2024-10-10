import type {BattleModel} from "@/playground/data/model";
import type {BattleModelState} from "@/playground/data/state";

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

export interface BattleState {
  id: string
  time: number
  state: BattleModelState
}
