import type {Position, Size} from '@/playground/data/common'
import {computed, type Ref} from 'vue'
import {BattleUtils} from "~/playground/utils/battle-utils";
import {Segment, Trapeze} from "~/playground/data/geometry";
import {useUserSettingsStore} from "~/stores/user-settings";
import {AppearancesNames} from "~/dictionary/appearances-names";

export interface DrawerBase {
  drawSegment: (ctx: CanvasRenderingContext2D, segment: Segment, lineWidth: number, color?: string) => void
  drawTrapeze: (ctx: CanvasRenderingContext2D, trapeze: Trapeze, color?: string) => void
  drawPolygon: (ctx: CanvasRenderingContext2D, polygon: Position[], color?: string) => void
  transformPosition: (position: Position) => (Position)
  scale: (value: number) => number
  getFont: (size: number) => string
  getGroundFillStyle: (ctx: CanvasRenderingContext2D) => CanvasPattern | string
}

export function useDrawerBase(scaleCoefficient: Ref<number>, canvasSize: Ref<Size>): DrawerBase {
  const battleStore = useBattleStore()
  const userSettingsStore = useUserSettingsStore()

  const appearances = computed(() => userSettingsStore.appearancesOrDefaultsNameValueMapping)

  const img = new Image()
  img.src = `/images/ground-texture-${battleStore.battle?.model.room.config.groundTexture}.jpg`

  function drawSegment(ctx: CanvasRenderingContext2D, segment: Segment, lineWidth: number, color?: string) {
    const begin = transformPosition(segment.begin)
    const end = transformPosition(segment.end)
    ctx.beginPath()
    ctx.lineWidth = lineWidth
    if (color) ctx.strokeStyle = color
    ctx.moveTo(begin.x, begin.y)
    ctx.lineTo(end.x, end.y)
    ctx.stroke()
    ctx.closePath()
  }

  function drawTrapeze(ctx: CanvasRenderingContext2D, trapeze: Trapeze, color?: string) {
    const angle = trapeze.position.angle
    const topCenter = BattleUtils.shiftedPosition(trapeze.position, trapeze.shape.height, angle + Math.PI / 2)
    const polygon = [
      BattleUtils.shiftedPosition(trapeze.position, -trapeze.shape.bottomRadius, angle),
      BattleUtils.shiftedPosition(trapeze.position, trapeze.shape.bottomRadius, angle),
      BattleUtils.shiftedPosition(topCenter, trapeze.shape.topRadius, angle),
      BattleUtils.shiftedPosition(topCenter, -trapeze.shape.topRadius, angle)
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

  function getGroundFillStyle(ctx: CanvasRenderingContext2D) {
    if (appearances.value[AppearancesNames.GROUND_TEXTURE_BACKGROUND] === '1') {
      return ctx.createPattern(img, 'repeat')!
    } else {
      return 'rgb(80 80 80)'
    }
  }

  return { drawSegment, drawTrapeze, drawPolygon, transformPosition, scale, getFont, getGroundFillStyle }
}
