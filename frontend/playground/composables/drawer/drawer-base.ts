import type {Position, Size} from '@/playground/data/common'
import {computed, type Ref} from 'vue'
import {BattleUtils} from "~/playground/utils/battle-utils";
import {Circle, HalfCircle, RegularPolygon, Segment, Trapeze} from "~/playground/data/geometry";
import {useUserSettingsStore} from "~/stores/user-settings";
import {AppearancesNames} from "~/dictionary/appearances-names";

export interface DrawParams {
  fillStyle?: CanvasPattern | string
  strokeStyle?: CanvasPattern | string
  lineWidth?: number
  stroke?: boolean
  groundTexture?: boolean
}

export interface CanvasText {
  position: Position
  text: string
  fontSize: number
  textWidth?: number
  textAlign?: CanvasTextAlign
}

export interface DrawerBase {
  drawMDIIcon: (path: string, position: Position, color: string) => void
  drawText: (canvasText: CanvasText, params?: DrawParams) => void
  drawSegment: (segment: Segment, params?: DrawParams) => void
  drawCircle: (circle: Circle, params?: DrawParams) => void
  drawHalfCircle: (halfCircle: HalfCircle, params?: DrawParams) => void
  drawArc: (center: Position, radius: number, startAngle: number, endAngle: number, params?: DrawParams) => void
  drawTrapeze: (trapeze: Trapeze, params?: DrawParams) => void
  drawRegularPolygon: (regularPolygon: RegularPolygon, params?: DrawParams) => void
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

  function drawMDIIcon(path: string, position: Position, color: string) {
    const pos = transformPosition(position)
    ctx.value!.save()
    ctx.value!.translate(pos.x - scale(0.18), pos.y - scale(0.25))
    const path2D = new Path2D(path)
    ctx.value!.fillStyle = color
    ctx.value!.fill(path2D)
    ctx.value!.restore()
  }

  function drawText(canvasText: CanvasText, params?: DrawParams) {
    const pos = transformPosition(canvasText.position)
    const width = canvasText.textWidth
        ? scale(canvasText.textWidth)
        : canvasText.fontSize * canvasText.text.length
    if (canvasText.textAlign) {
      ctx.value!.textAlign = canvasText.textAlign
    }
    params && setDrawParams(params)
    ctx.value!.beginPath()
    ctx.value!.font = getFont(canvasText.fontSize)
    ctx.value!.fillText(canvasText.text, pos.x, pos.y, width)
    ctx.value!.closePath()
  }

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
    drawArc(circle.center, circle.radius, 0, 2 * Math.PI, params)
  }

  function drawHalfCircle(halfCircle: HalfCircle, params?: DrawParams) {
    drawArc(halfCircle.center, halfCircle.radius, Math.PI - halfCircle.angle, 2 * Math.PI - halfCircle.angle, params)
  }

  function drawArc(center: Position, radius: number, startAngle: number, endAngle: number, params?: DrawParams) {
    const position = transformPosition(center)
    params && setDrawParams(params)
    ctx.value!.beginPath()
    ctx.value!.arc(position.x, position.y, scale(radius), startAngle, endAngle)
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

  function drawRegularPolygon(regularPolygon: RegularPolygon, params?: DrawParams) {
    const angleStep = 2 * Math.PI / regularPolygon.sidesNumber()
    const polygon: Position[] = []
    for (
        let angle = regularPolygon.position.angle + angleStep / 2;
        angle < regularPolygon.position.angle + Math.PI * 2;
        angle += angleStep
    ) {
      polygon.push(BattleUtils.shiftedPosition(regularPolygon.position, regularPolygon.radius(), angle))
    }
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
    if (appearances.value[AppearancesNames.GROUND_TEXTURE_BACKGROUND] === '1' && battleStore.groundTexture) {
      return ctx.value!.createPattern(toRaw(battleStore.groundTexture), 'repeat')!
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
    drawMDIIcon,
    drawText,
    drawSegment,
    drawCircle,
    drawHalfCircle,
    drawArc,
    drawTrapeze,
    drawRegularPolygon,
    drawPolygon,
    transformPosition,
    scale,
    getFont,
    getGroundFillStyle,
    setDrawParams
  }
}
