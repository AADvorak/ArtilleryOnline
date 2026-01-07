export const ShapeNames = {
  CIRCLE: 'Circle',
  HALF_CIRCLE: 'HalfCircle',
  TRAPEZE: 'Trapeze',
  REGULAR_POLYGON: 'RegularPolygon',
}

export interface Shape {
  name: string
}

export interface CircleShape extends Shape {
  radius: number
}

export interface HalfCircleShape extends Shape {
  radius: number
}

export interface TrapezeShape extends Shape {
  bottomRadius: number
  topRadius: number
  height: number
}

export interface RegularPolygonShape extends Shape {
  radius: number
  sidesNumber: number
}
