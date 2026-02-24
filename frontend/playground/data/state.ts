import type {Ammo, Position, BodyVelocity, Velocity, BodyPosition, BodyAcceleration} from "@/playground/data/common";
import {MovingDirection} from "@/playground/data/common";

export interface BodyState {
  position: BodyPosition
  velocity: BodyVelocity
  pushingDirection?: MovingDirection
  rotatingDirection?: MovingDirection
}

export interface GunState {
  angle: number
  targetAngle: number
  rotatingDirection: MovingDirection | null
  rotatingTime: number
  loadedShell?: string
  selectedShell?: string
  loadingShell?: string
  loadRemainTime: number
  triggerPushed: boolean
  fixed: boolean
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
  firstStepPassed?: boolean
}

export interface BodyParticleState extends BodyState {
  remainTime: number
  firstStepPassed?: boolean
  acceleration?: BodyAcceleration
}

export interface MissileState extends BodyState {
}

export interface DroneState extends BodyState {
  ammo: Ammo
  gunState: GunState
  gunAngle: number
  destroyed: boolean
}

export interface BoxState extends BodyState {
}

export interface DroneInVehicleState {
  launched: boolean
  readyToLaunch: boolean
  prepareToLaunchRemainTime: number
  remainDrones: number
}

export interface MissileLauncherState {
  prepareToLaunchRemainTime: number
  remainMissiles: number
}

export interface BomberState {
  readyToFlight: boolean
  flying: boolean
  prepareToFlightRemainTime: number
  remainFlights: number
}

export interface ExplosionState {
  time: number
  radius: number
  position: Position
}

export interface VehicleState extends BodyState {
  movingDirection?: MovingDirection
  hitPoints: number
  ammo: Ammo
  gunState: GunState
  trackState: TrackState
  jetState: JetState
  droneState: DroneInVehicleState
  bomberState?: BomberState
  missileLauncherState?: MissileLauncherState
  onGround: boolean
}

export interface SurfaceState {
  begin: Position
  end: Position
  width: number
}

export interface RoomState {
  groundLine?: number[]
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

export interface BoxStates {
  [id: number]: BoxState
}

export interface BattleModelState {
  vehicles?: VehicleStates
  shells?: ShellStates
  missiles?: MissileStates
  drones?: DroneStates
  boxes?: BoxStates
}
