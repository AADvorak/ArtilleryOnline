export interface Position {
  x: number
  y: number
}

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
