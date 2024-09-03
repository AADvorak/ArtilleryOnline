import type {Ammo, Position} from "@/data/common";
import {MovingDirection} from "@/data/common";

export interface GunState {
  loadedShell: string
  selectedShell: string
  loadingShell: string
  loadRemainTime: number
  triggerPushed: boolean
}

export interface ShellState {
  position: Position
  angle: number
  velocity: number
}

export interface VehicleState {
  position: Position
  movingDirection: MovingDirection
  angle: number
  gunAngle: number
  gunRotatingDirection: MovingDirection
  hitPoints: number
  ammo: Ammo
  gunState: GunState
}
