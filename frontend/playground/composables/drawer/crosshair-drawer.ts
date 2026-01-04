import type {DrawerBase} from '@/playground/composables/drawer/drawer-base'
import type {Ref} from 'vue'
import {useBattleStore} from '~/stores/battle'
import {BattleUtils} from "~/playground/utils/battle-utils";
import {Segment} from "~/playground/data/geometry";
import {type Position, type TargetData} from "~/playground/data/common";
import {VectorUtils} from "~/playground/utils/vector-utils";

const CROSSHAIR_RADIUS_X = 0.3
const CROSSHAIR_RADIUS_Y = 0.1
const CROSSHAIR_COLORS = {
  DEFAULT: 'rgb(255,255,255)',
  HARD_PENETRATION: 'rgb(255,3,3)',
  MODERATE_PENETRATION: 'rgb(255,98,1)',
  MODERATE_EASY_PENETRATION: 'rgb(255,204,1)',
  EASY_PENETRATION: 'rgb(93,252,2)',
}

export function useCrosshairDrawer(
    drawerBase: DrawerBase,
    ctx: Ref<CanvasRenderingContext2D | undefined>
) {
  const battleStore = useBattleStore()

  function draw() {
    const targetData = battleStore.targetData
    if (targetData) {
      drawCrosshair(
          targetData.contact.position,
          VectorUtils.getAngle(targetData.hitNormal),
          getCrosshairColor(targetData)
      )
    }
  }

  function drawCrosshair(position: Position, angle: number, color: string) {
    drawerBase.drawSegment(ctx.value!, new Segment(
        BattleUtils.shiftedPosition(position, CROSSHAIR_RADIUS_Y, angle),
        BattleUtils.shiftedPosition(position, -CROSSHAIR_RADIUS_Y, angle),
    ), 2, color)
    drawerBase.drawSegment(ctx.value!, new Segment(
        BattleUtils.shiftedPosition(position, CROSSHAIR_RADIUS_X, angle - Math.PI / 2),
        BattleUtils.shiftedPosition(position, -CROSSHAIR_RADIUS_X, angle - Math.PI / 2),
    ), 2, color)
    const center = drawerBase.transformPosition(position)
    const radiusX = drawerBase.scale(CROSSHAIR_RADIUS_X)
    const radiusY = drawerBase.scale(CROSSHAIR_RADIUS_Y)
    ctx.value!.strokeStyle = color
    ctx.value!.beginPath()
    ctx.value!.ellipse(center.x, center.y, radiusX, radiusY, 3 * Math.PI / 2 - angle, 0, 2 * Math.PI)
    ctx.value!.stroke()
    ctx.value!.closePath()
  }

  function getCrosshairColor(targetData: TargetData) {
    if (!targetData.armor || !targetData.penetration) {
      return CROSSHAIR_COLORS.DEFAULT
    }
    const angleCoefficient = VectorUtils.dotProduct(targetData.hitNormal, targetData.contact.normal)
    const resultPenetration = angleCoefficient * angleCoefficient * targetData.penetration
    const ratio = resultPenetration / targetData.armor
    if (ratio > 1.1) {
      return CROSSHAIR_COLORS.EASY_PENETRATION
    }
    if (ratio > 1.0) {
      return CROSSHAIR_COLORS.MODERATE_EASY_PENETRATION
    }
    if (ratio > 0.9) {
      return CROSSHAIR_COLORS.MODERATE_PENETRATION
    }
    return CROSSHAIR_COLORS.HARD_PENETRATION
  }

  return {draw}
}
