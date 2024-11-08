import type { Position, Size } from '@/playground/data/common'
import type { Ref } from 'vue'

export interface DrawerBase {
  transformPosition: (position: Position) => (Position)
  scale: (value: number) => number
}

export function useDrawerBase(scaleCoefficient: Ref<number>, canvasSize: Ref<Size>) {
  function transformPosition(position: Position) {
    return {
      x: Math.floor(scaleCoefficient.value * position.x),
      y: Math.floor(canvasSize.value.height - scaleCoefficient.value * position.y)
    }
  }

  function scale(value: number) {
    return value * scaleCoefficient.value
  }

  return { transformPosition, scale }
}
