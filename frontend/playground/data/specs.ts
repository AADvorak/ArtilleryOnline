import {type Position, ShellType} from "@/playground/data/common";

export interface ShellSpecs {
  velocity: number
  damage: number
  radius: number
  caliber: number
  type: ShellType
}

export interface ExplosionSpecs {
  radius: number
  duration: number
}

export interface AvailableShells {
  [key: string]: ShellSpecs
}

export interface GunSpecs {
  loadTime: number
  rotationVelocity: number
  length: number
  caliber: number
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
  groundReactionCoefficient: number
  groundFrictionCoefficient: number
  groundMaxDepth: number
}

export interface VehicleSpecs {
  acceleration: number;
  hitPoints: number
  ammo: number
  minAngle: number
  maxAngle: number
  movingVelocity: number
  radius: number
  wheelRadius: number
  hullRadius: number
  availableGuns: AvailableGuns
}

export interface JetSpecs {
  capacity: number
  consumption: number
  regeneration: number
  acceleration: number
}
