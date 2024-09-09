import type {BattleModel} from "@/data/model";

export enum BattleStage {
  WAITING = 'WAITING',
  ACTIVE = 'ACTIVE',
  FINISHED = 'FINISHED',
  TERMINATE = 'TERMINATE'
}

export interface Battle {
  id: string
  model: BattleModel
  time: number
  battleStage: BattleStage
}
