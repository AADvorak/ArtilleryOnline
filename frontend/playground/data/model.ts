import type {
  BoxSpecs,
  DroneSpecs,
  ExplosionSpecs,
  MissileSpecs,
  RoomSpecs,
  ShellSpecs,
  VehicleSpecs
} from "@/playground/data/specs";
import type {
  BodyParticleState,
  BodyState,
  BoxState,
  DroneState,
  ExplosionState,
  MissileState,
  ParticleState,
  RoomState,
  ShellState,
  VehicleState
} from "@/playground/data/state";
import type {
  BodyParticleConfig,
  BoxConfig,
  DroneConfig,
  ParticleConfig,
  RoomConfig,
  VehicleConfig
} from "@/playground/data/config";
import type {Shift} from "~/playground/data/common";

export interface BodyModel {
  id: number
  preCalc: BodyPreCalc
  state: BodyState
}

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
  config: ParticleConfig
}

export interface BodyParticleModel {
  id: number
  state: BodyParticleState
  config: BodyParticleConfig
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
  preCalc: DronePreCalc
  config: DroneConfig
  state: DroneState
}

export interface BoxModel {
  id: number
  specs: BoxSpecs
  preCalc: BoxPreCalc
  config: BoxConfig
  state: BoxState
}

export interface ExplosionModel {
  id: number
  specs: ExplosionSpecs
  state: ExplosionState
}

export interface BodyPreCalc {
  mass: number
  momentOfInertia: number
  centerOfMassShift: Shift
  maxRadius: number
}

export interface VehiclePreCalc extends BodyPreCalc {
  wheelDistance: number
  wheelAngle: number
}

export interface DronePreCalc extends BodyPreCalc {
}

export interface BoxPreCalc extends BodyPreCalc {
}

export interface VehicleModel extends BodyModel {
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

export interface BodyParticleModels {
  [id: number]: BodyParticleModel
}

export interface BoxModels {
  [id: number]: BoxModel
}

export interface BattleModel {
  room: RoomModel
  shells: ShellModels
  explosions: ExplosionModels
  vehicles: VehicleModels
  missiles: MissileModels
  drones: DroneModels
  boxes: BoxModels
  statistics: PlayersBattleStatistics
  updated: boolean
}

export interface PlayerBattleStatistics {
  causedDamage: number
  destroyedVehicles: number
}

export interface PlayersBattleStatistics {
  [nickname: string]: PlayerBattleStatistics
}
