import {BoxType, JetType, type Position, ShellType, HitSurface} from "@/playground/data/common";
import type {HalfCircleShape, Shape, TrapezeShape} from "~/playground/data/shapes";

export interface ShellSpecs {
  velocity: number
  damage: number
  radius: number
  mass: number
  caliber: number
  penetration: number
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
  ammo: number
  loadTime: number
  rotationVelocity: number
  slowRotationVelocity: number
  slowToFastRotationTime: number
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

export interface AvailableDrones {
  [key: string]: DroneSpecs
}

export interface AvailableBombers {
  [key: string]: BomberSpecs
}

export interface Armor {
  [key: string]: number
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
  surfaceMaxDepth: number
}

export interface VehicleSpecs {
  name: string
  acceleration: number
  wheelAngleVelocity: number
  hitPoints: number
  missiles: number
  minAngle: number
  maxAngle: number
  turretShape: Shape | HalfCircleShape | TrapezeShape
  armor: Armor
  wheelRadius: number
  hullRadius: number
  trackRepairTime: number
  minTrackHitCaliber: number
  availableGuns: AvailableGuns
  availableJets: AvailableJets
  availableMissiles: AvailableMissiles
  availableDrones: AvailableDrones
  availableBombers: AvailableBombers
  defaultGun: string
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

export interface DroneSpecs {
  maxEngineAcceleration: number
  hullRadius: number
  enginesRadius: number
  mass: number
  flyHeight: number
  criticalAngle: number
  prepareToLaunchTime: number
  availableGuns: AvailableGuns
}

export interface BoxSpecs {
  shape: Shape | TrapezeShape
  mass: number
  type: BoxType
}

export interface BomberSpecs {
  flights: number
  prepareToFlightTime: number
  flightTime: number
  bombs: ShellSpecs
}
