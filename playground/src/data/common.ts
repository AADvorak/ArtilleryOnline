export interface Position {
  x: number
  y: number
}

export interface Ammo {
  [key: string]: number
}

export enum MovingDirection {
  LEFT = 'LEFT',
  RIGHT = 'RIGHT'
}
