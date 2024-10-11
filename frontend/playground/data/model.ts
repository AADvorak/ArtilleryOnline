import type {ExplosionSpecs, RoomSpecs, ShellSpecs, VehicleSpecs} from "@/playground/data/specs";
import type {ExplosionState, RoomState, ShellState, VehicleState} from "@/playground/data/state";
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

export interface ExplosionModel {
  id: number
  specs: ExplosionSpecs
  state: ExplosionState
}

export interface VehiclePreCalc {
  frictionCoefficient: number;
  wheelDistance: number
  wheelAngle: number
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

export interface ExplosionModels {
  [id: number]: ExplosionModel
}

export interface BattleModel {
  room: RoomModel
  shells: ShellModels
  explosions: ExplosionModels
  vehicles: VehicleModels
  updated: boolean
}
