export interface Size {
  width: number
  height: number
}

export interface Ammo {
  [key: string]: number
}

export enum MovingDirection {
  LEFT = 'LEFT',
  RIGHT = 'RIGHT'
}

export enum ShellType {
  AP = 'AP',
  HE = 'HE'
}

export interface ApplicationSettings {
  debug: boolean,
  clientProcessing: boolean
}

export interface Vector {
  x: number
  y: number
}

export interface Acceleration extends Vector {
}

export interface Velocity extends Vector {
}

export interface Position extends Vector {
}

export interface VehicleVelocity {
  x: number
  y: number
  angle: number
}

export interface VehicleAcceleration {
  x: number
  y: number
  angle: number
}
