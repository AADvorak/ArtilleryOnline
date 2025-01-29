import type {ExplosionSpecs, MissileSpecs, RoomSpecs, ShellSpecs, VehicleSpecs} from "@/playground/data/specs";
import type {ExplosionState, MissileState, RoomState, ShellState, VehicleState} from "@/playground/data/state";
import type {VehicleConfig} from "@/playground/data/config";

export interface RoomModel {
  specs: RoomSpecs
  state: RoomState
}

export interface ShellModel {
  id: number
  specs: ShellSpecs
  state: ShellState
}

export interface MissileModel {
  id: number
  vehicleId: number
  specs: MissileSpecs
  state: MissileState
}

export interface ExplosionModel {
  id: number
  specs: ExplosionSpecs
  state: ExplosionState
}

export interface VehiclePreCalc {
  wheelDistance: number
  wheelAngle: number
  mass: number
}

export interface VehicleModel {
  id: number
  specs: VehicleSpecs
  config: VehicleConfig
  state: VehicleState
  preCalc: VehiclePreCalc
}

export interface VehicleModels {
  [userKey: string]: VehicleModel
}

export interface ShellModels {
  [id: number]: ShellModel
}

export interface MissileModels {
  [id: number]: MissileModel
}

export interface ExplosionModels {
  [id: number]: ExplosionModel
}

export interface BattleModel {
  room: RoomModel
  shells: ShellModels
  explosions: ExplosionModels
  vehicles: VehicleModels
  missiles: MissileModels
  updated: boolean
}
