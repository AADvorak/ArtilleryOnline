import type {ExplosionModel, ShellModel} from "~/playground/data/model";

export interface BattleModelAdded {
  shells?: ShellModel[]
  explosions?: ExplosionModel[]
}

export interface BattleModelRemoved {
  shellIds?: number[]
  explosionIds?: number[]
  vehicleKeys?: string[]
}

export interface RoomStateUpdate {
  begin: number
  groundLinePart: number[]
}

export interface BattleModelUpdates {
  added?: BattleModelAdded
  removed?: BattleModelRemoved
  roomStateUpdates?: RoomStateUpdate[]
}
