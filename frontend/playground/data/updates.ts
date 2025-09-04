import type {BoxModel, DroneModel, ExplosionModel, MissileModel, ShellModel} from "~/playground/data/model";

export interface BattleModelAdded {
  shells?: ShellModel[]
  missiles?: MissileModel[]
  explosions?: ExplosionModel[]
  drones?: DroneModel[]
  boxes?: BoxModel[]
}

export interface BattleModelRemoved {
  shellIds?: number[]
  missileIds?: number[]
  explosionIds?: number[]
  droneIds?: number[]
  boxIds?: number[]
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
