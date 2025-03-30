import type {Ammo, Position, BodyVelocity, Velocity, BodyPosition, Missiles} from "@/playground/data/common";
import {MovingDirection} from "@/playground/data/common";

export interface GunState {
  angle: number
  targetAngle: number
  rotatingDirection: MovingDirection
  loadedShell?: string
  selectedShell?: string
  loadingShell?: string
  loadRemainTime: number
  triggerPushed: boolean
}

export interface TrackState {
  broken: boolean
  repairRemainTime: number
}

export interface JetState {
  volume: number
  active: boolean
}

export interface ShellState {
  position: Position
  velocity: Velocity
  stuck: boolean
}

export interface ParticleState {
  position: Position
  velocity: Velocity
  remainTime: number
}

export interface MissileState {
  position: BodyPosition
  velocity: BodyVelocity
}

export interface DroneState {
  position: BodyPosition
  velocity: BodyVelocity
  ammo: Ammo
  gunState: GunState
  gunAngle: number
  destroyed: boolean
}

export interface DroneInVehicleState {
  launched: boolean
  readyToLaunch: boolean
  prepareToLaunchRemainTime: number
}

export interface BomberState {
  readyToFlight: boolean
  flying: boolean
  remainFlights: number
}

export interface ExplosionState {
  time: number
  radius: number
  position: Position
}

export interface VehicleState {
  position: BodyPosition
  velocity: BodyVelocity
  movingDirection: MovingDirection
  hitPoints: number
  ammo: Ammo
  missiles: Missiles
  gunState: GunState
  trackState: TrackState
  jetState: JetState
  droneState: DroneInVehicleState
  bomberState?: BomberState
  onGround: boolean
}

export interface SurfaceState {
  begin: Position
  end: Position
}

export interface RoomState {
  groundLine: number[]
  surfaces?: SurfaceState[]
}

export interface VehicleStates {
  [userKey: string]: VehicleState
}

export interface ShellStates {
  [id: number]: ShellState
}

export interface MissileStates {
  [id: number]: MissileState
}

export interface DroneStates {
  [id: number]: DroneState
}

export interface BattleModelState {
  vehicles?: VehicleStates
  shells?: ShellStates
  missiles?: MissileStates
  drones?: DroneStates
}
