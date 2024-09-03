import type {RoomSpecs, ShellSpecs, VehicleSpecs} from "@/data/specs";
import type {ShellState, VehicleState} from "@/data/state";
import type {VehicleConfig} from "@/data/config";

export interface RoomModel {
  specs: RoomSpecs
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

export interface BattleModel {
  room: RoomModel
  shells: ShellModel[]
  vehicles: VehicleModels
}
