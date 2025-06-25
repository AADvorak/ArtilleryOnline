export const ShapeNames = {
  HALF_CIRCLE: 'HalfCircle',
  TRAPEZE: 'Trapeze'
}

export interface Shape {
  name: string
}

export interface HalfCircleShape extends Shape {
  radius: number
}

export interface TrapezeShape extends Shape {
  bottomRadius: number
  topRadius: number
  height: number
}
