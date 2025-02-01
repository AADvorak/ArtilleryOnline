import type {ExplosionModel, MissileModel, ShellModel} from "~/playground/data/model";

export interface BattleModelAdded {
  shells?: ShellModel[]
  missiles?: MissileModel[]
  explosions?: ExplosionModel[]
}

export interface BattleModelRemoved {
  shellIds?: number[]
  missileIds?: number[]
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
