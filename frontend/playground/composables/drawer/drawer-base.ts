import type {BodyPosition, Position, Size} from '@/playground/data/common'
import type { Ref } from 'vue'
import type {TrapezeShape} from "~/playground/data/shapes";
import {BattleUtils} from "~/playground/utils/battle-utils";

export interface DrawerBase {
  drawTrapeze: (ctx: CanvasRenderingContext2D, bottomCenter: BodyPosition, shape: TrapezeShape, color?: string) => void
  drawPolygon: (ctx: CanvasRenderingContext2D, polygon: Position[], color?: string) => void
  transformPosition: (position: Position) => (Position)
  scale: (value: number) => number
  getFont: (size: number) => string
}

export function useDrawerBase(scaleCoefficient: Ref<number>, canvasSize: Ref<Size>) {

  function drawTrapeze(ctx: CanvasRenderingContext2D, bottomCenter: BodyPosition,
                       shape: TrapezeShape, color?: string) {
    const angle = bottomCenter.angle
    const topCenter = BattleUtils.shiftedPosition(bottomCenter, shape.height, angle + Math.PI / 2)
    const polygon = [
      BattleUtils.shiftedPosition(bottomCenter, -shape.bottomRadius, angle),
      BattleUtils.shiftedPosition(bottomCenter, shape.bottomRadius, angle),
      BattleUtils.shiftedPosition(topCenter, shape.topRadius, angle),
      BattleUtils.shiftedPosition(topCenter, -shape.topRadius, angle)
    ]
    drawPolygon(ctx, polygon, color)
  }

  function drawPolygon(ctx: CanvasRenderingContext2D, polygon: Position[], color?: string) {
    if (color) ctx.fillStyle = color
    ctx.lineWidth = 1
    ctx.beginPath()
    const firstPosition = transformPosition(polygon[0]!)
    ctx.moveTo(firstPosition.x, firstPosition.y)
    for (let i = 1; i < polygon.length; i++) {
      const position = transformPosition(polygon[i]!)
      ctx.lineTo(position.x, position.y)
    }
    ctx.fill()
    ctx.closePath()
  }

  function transformPosition(position: Position) {
    return {
      x: Math.floor(scaleCoefficient.value * position.x),
      y: Math.floor(canvasSize.value.height - scaleCoefficient.value * position.y)
    }
  }

  function scale(value: number) {
    return value * scaleCoefficient.value
  }

  function getFont(size: number) {
    return `bold ${size}px Roboto, sans-serif`
  }

  return { drawTrapeze, drawPolygon, transformPosition, scale, getFont }
}
