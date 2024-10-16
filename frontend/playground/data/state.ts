import type {Ammo, Position, VehicleVelocity} from "@/playground/data/common";
import {MovingDirection} from "@/playground/data/common";

export interface GunState {
  loadedShell: string
  selectedShell: string
  loadingShell: string
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
  angle: number
  velocity: number
}

export interface ExplosionState {
  time: number
  radius: number
  position: Position
}

export interface VehicleState {
  velocity: VehicleVelocity;
  position: Position
  movingDirection: MovingDirection
  angle: number
  gunAngle: number
  gunRotatingDirection: MovingDirection
  hitPoints: number
  ammo: Ammo
  gunState: GunState
  trackState: TrackState
  jetState: JetState
}

export interface RoomState {
  groundLine: number[]
}

export interface VehicleStates {
  [userKey: string]: VehicleState
}

export interface ShellStates {
  [id: number]: ShellState
}

export interface BattleModelState {
  vehicles: VehicleStates
  shells: ShellStates
}
