export interface Size {
  width: number
  height: number
}

export interface Ammo {
  [key: string]: number
}

export interface Missiles {
  [key: string]: number
}

export enum MovingDirection {
  LEFT = 'LEFT',
  RIGHT = 'RIGHT',
  UP = 'UP',
  DOWN = 'DOWN'
}

export enum ShellType {
  AP = 'AP',
  HE = 'HE',
  SGN = 'SGN',
  BMB = 'BMB'
}

export enum JetType {
  VERTICAL = 'VERTICAL',
  HORIZONTAL = 'HORIZONTAL'
}

export enum ShellHitType {
  GROUND = 'GROUND',
  VEHICLE_HULL = 'VEHICLE_HULL',
  VEHICLE_TRACK = 'VEHICLE_TRACK',
  DRONE = 'DRONE'
}

export enum CollideObjectType {
  WALL = 'WALL',
  GROUND = 'GROUND',
  VEHICLE = 'VEHICLE'
}

export interface CollideObject {
  type: CollideObjectType
  vehicleId?: number
}

export interface ApplicationSettings {
  debug: boolean
  showShellTrajectory: boolean
  clientProcessing: boolean
  userBattleQueueTimeout: number
}

export interface TimeZone {
  offset: number
}

export interface Vector {
  x: number
  y: number
}

export interface BodyVector extends Vector {
  angle: number
}

export interface Acceleration extends Vector {
}

export interface Velocity extends Vector {
}

export interface Position extends Vector {
}

export interface BodyVelocity extends BodyVector {
}

export interface BodyAcceleration extends BodyVector{
}

export interface BodyPosition extends BodyVector {
}

export interface Shift {
  distance: number
  angle: number
}

export interface Contact {
  depth: number
  angle: number
  normal: Vector
  position: Position
  description?: string
}
