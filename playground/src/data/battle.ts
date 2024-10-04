import type {BattleModel} from "@/data/model";

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
