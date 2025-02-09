import {JetType, type Position, ShellType} from "@/playground/data/common";

export interface ShellSpecs {
  velocity: number
  damage: number
  radius: number
  mass: number
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

export interface AvailableJets {
  [key: string]: JetSpecs
}

export interface AvailableMissiles {
  [key: string]: MissileSpecs
}

export interface RoomSpecs {
  leftBottom: Position
  rightTop: Position
  step: number
  gravityAcceleration: number
  groundReactionCoefficient: number
  groundFrictionCoefficient: number
  airFrictionCoefficient: number
  groundMaxDepth: number
}

export interface VehicleSpecs {
  name: string
  acceleration: number
  hitPoints: number
  ammo: number
  missiles: number
  minAngle: number
  maxAngle: number
  radius: number
  wheelRadius: number
  hullRadius: number
  trackRepairTime: number
  minTrackHitCaliber: number
  availableGuns: AvailableGuns
  availableJets: AvailableJets
  availableMissiles: AvailableMissiles
}

export interface JetSpecs {
  capacity: number
  consumption: number
  regeneration: number
  acceleration: number
  type: JetType
}

export interface MissileSpecs {
  pushingAcceleration: number
  correctingAccelerationCoefficient: number
  minCorrectingVelocity: number
  anglePrecision: number
  damage: number
  radius: number
  mass: number
  caliber: number
  length: number
}
