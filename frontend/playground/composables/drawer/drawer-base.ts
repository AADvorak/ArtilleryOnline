import type {Position, Size} from '@/playground/data/common'
import {computed, type Ref} from 'vue'
import {BattleUtils} from "~/playground/utils/battle-utils";
import {Circle, Segment, Trapeze} from "~/playground/data/geometry";
import {useUserSettingsStore} from "~/stores/user-settings";
import {AppearancesNames} from "~/dictionary/appearances-names";

export interface DrawParams {
  fillStyle?: CanvasPattern | string
  strokeStyle?: CanvasPattern | string
  lineWidth?: number
  stroke?: boolean
  groundTexture?: boolean
}

export interface DrawerBase {
  drawSegment: (segment: Segment, params?: DrawParams) => void
  drawCircle: (circle: Circle, params?: DrawParams) => void
  drawTrapeze: (trapeze: Trapeze, params?: DrawParams) => void
  drawPolygon: (polygon: Position[], params?: DrawParams) => void
  transformPosition: (position: Position) => (Position)
  scale: (value: number) => number
  getFont: (size: number) => string
  getGroundFillStyle: () => CanvasPattern | string
  setDrawParams: (params: DrawParams) => void
}

export function useDrawerBase(
    ctx: Ref<CanvasRenderingContext2D | undefined>,
    scaleCoefficient: Ref<number>,
    canvasSize: Ref<Size>
): DrawerBase {
  const battleStore = useBattleStore()
  const userSettingsStore = useUserSettingsStore()

  const appearances = computed(() => userSettingsStore.appearancesOrDefaultsNameValueMapping)

  const img = new Image()
  img.src = `/images/ground-texture-${battleStore.battle?.model.room.config.groundTexture}.jpg`

  function drawSegment(segment: Segment, params?: DrawParams) {
    const begin = transformPosition(segment.begin)
    const end = transformPosition(segment.end)
    params && setDrawParams(params)
    ctx.value!.beginPath()
    ctx.value!.moveTo(begin.x, begin.y)
    ctx.value!.lineTo(end.x, end.y)
    ctx.value!.stroke()
    ctx.value!.closePath()
  }

  function drawCircle(circle: Circle, params?: DrawParams) {
    const center = transformPosition(circle.center)
    const radius = scale(circle.radius)
    params && setDrawParams(params)
    ctx.value!.beginPath()
    ctx.value!.arc(center.x, center.y, radius, 0, 2 * Math.PI)
    params?.stroke ? ctx.value!.stroke() : ctx.value!.fill()
    ctx.value!.closePath()
  }

  function drawTrapeze(trapeze: Trapeze, params?: DrawParams) {
    const angle = trapeze.position.angle
    const topCenter = BattleUtils.shiftedPosition(trapeze.position, trapeze.shape.height, angle + Math.PI / 2)
    const polygon = [
      BattleUtils.shiftedPosition(trapeze.position, -trapeze.shape.bottomRadius, angle),
      BattleUtils.shiftedPosition(trapeze.position, trapeze.shape.bottomRadius, angle),
      BattleUtils.shiftedPosition(topCenter, trapeze.shape.topRadius, angle),
      BattleUtils.shiftedPosition(topCenter, -trapeze.shape.topRadius, angle)
    ]
    drawPolygon(polygon, params)
  }

  function drawPolygon(polygon: Position[], params?: DrawParams) {
    params && setDrawParams(params)
    ctx.value!.beginPath()
    const firstPosition = transformPosition(polygon[0]!)
    ctx.value!.moveTo(firstPosition.x, firstPosition.y)
    for (let i = 1; i < polygon.length; i++) {
      const position = transformPosition(polygon[i]!)
      ctx.value!.lineTo(position.x, position.y)
    }
    params?.stroke ? ctx.value!.stroke() : ctx.value!.fill()
    ctx.value!.closePath()
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

  function getGroundFillStyle() {
    if (appearances.value[AppearancesNames.GROUND_TEXTURE_BACKGROUND] === '1') {
      return ctx.value!.createPattern(img, 'repeat')!
    } else {
      return 'rgb(80 80 80)'
    }
  }

  function setDrawParams(params: DrawParams) {
    if (params.groundTexture) {
      ctx.value!.fillStyle = getGroundFillStyle()
    } else if (params.fillStyle) {
      ctx.value!.fillStyle = params.fillStyle
    }
    if (params.strokeStyle) {
      ctx.value!.strokeStyle = params.strokeStyle
    }
    if (params.lineWidth) {
      ctx.value!.lineWidth = params.lineWidth
    }
  }

  return {
    drawSegment,
    drawCircle,
    drawTrapeze,
    drawPolygon,
    transformPosition,
    scale,
    getFont,
    getGroundFillStyle,
    setDrawParams
  }
}
