import type {Ammo, Position} from "@/data/common";
import {MovingDirection} from "@/data/common";

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

export interface ShellState {
  position: Position
  angle: number
  velocity: number
}

export interface VehicleState {
  velocity: number;
  position: Position
  movingDirection: MovingDirection
  angle: number
  gunAngle: number
  gunRotatingDirection: MovingDirection
  hitPoints: number
  ammo: Ammo
  gunState: GunState
  trackState: TrackState
}

export interface RoomState {
  groundLine: number[]
}
