import type {Position} from "@/data/common";

export interface ShellSpecs {
  velocity: number
  damage: number
  radius: number
}

export interface AvailableShells {
  [key: string]: ShellSpecs
}

export interface GunSpecs {
  loadTime: number
  rotationVelocity: number
  length: number
  availableShells: AvailableShells
}

export interface AvailableGuns {
  [key: string]: GunSpecs
}

export interface RoomSpecs {
  leftBottom: Position
  rightTop: Position
  step: number
  gravityAcceleration: number
}

export interface VehicleSpecs {
  acceleration: number;
  hitPoints: number
  ammo: number
  minAngle: number
  maxAngle: number
  criticalAngle: number
  movingVelocity: number
  radius: number
  wheelRadius: number
  availableGuns: AvailableGuns
}
