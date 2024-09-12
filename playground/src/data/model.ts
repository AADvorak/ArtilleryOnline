import type {RoomSpecs, ShellSpecs, VehicleSpecs} from "@/data/specs";
import type {RoomState, ShellState, VehicleState} from "@/data/state";
import type {VehicleConfig} from "@/data/config";

export interface RoomModel {
  specs: RoomSpecs
  state: RoomState
}

export interface ShellModel {
  id: number
  specs: ShellSpecs
  state: ShellState
}

export interface VehicleModel {
  id: number
  specs: VehicleSpecs
  config: VehicleConfig
  state: VehicleState
}

export interface VehicleModels {
  [userKey: string]: VehicleModel
}

export interface ShellModels {
  [id: number]: ShellModel
}

export interface BattleModel {
  room: RoomModel
  shells: ShellModels
  vehicles: VehicleModels
}
