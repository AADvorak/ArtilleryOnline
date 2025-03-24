import type {
  DroneSpecs,
  ExplosionSpecs,
  MissileSpecs,
  RoomSpecs,
  ShellSpecs,
  VehicleSpecs
} from "@/playground/data/specs";
import type {
  DroneState,
  ExplosionState,
  MissileState,
  ParticleState,
  RoomState,
  ShellState,
  VehicleState
} from "@/playground/data/state";
import type {DroneConfig, RoomConfig, VehicleConfig} from "@/playground/data/config";

export interface RoomModel {
  specs: RoomSpecs
  state: RoomState
  config: RoomConfig
}

export interface ShellModel {
  id: number
  specs: ShellSpecs
  state: ShellState
}

export interface ParticleModel {
  id: number
  state: ParticleState
}

export interface MissileModel {
  id: number
  vehicleId: number
  specs: MissileSpecs
  state: MissileState
}

export interface DroneModel {
  id: number
  vehicleId?: number
  specs: DroneSpecs
  config: DroneConfig
  state: DroneState
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

export interface DroneModels {
  [id: number]: DroneModel
}

export interface ExplosionModels {
  [id: number]: ExplosionModel
}

export interface ParticleModels {
  [id: number]: ParticleModel
}

export interface BattleModel {
  room: RoomModel
  shells: ShellModels
  explosions: ExplosionModels
  vehicles: VehicleModels
  missiles: MissileModels
  drones: DroneModels
  updated: boolean
}
