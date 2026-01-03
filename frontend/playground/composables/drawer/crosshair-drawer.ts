import type {DrawerBase} from '@/playground/composables/drawer/drawer-base'
import type {Ref} from 'vue'
import {useBattleStore} from '~/stores/battle'
import {BattleUtils} from "~/playground/utils/battle-utils";
import {Segment} from "~/playground/data/geometry";
import {type Position} from "~/playground/data/common";
import {VectorUtils} from "~/playground/utils/vector-utils";

const CROSSHAIR_RADIUS_X = 0.3
const CROSSHAIR_RADIUS_Y = 0.1
const CROSSHAIR_COLOR = 'rgb(256,256,256)'

export function useCrosshairDrawer(
    drawerBase: DrawerBase,
    ctx: Ref<CanvasRenderingContext2D | undefined>
) {
  const battleStore = useBattleStore()

  function draw() {
    const targetData = battleStore.targetData
    if (targetData) {
      drawCrosshair(targetData.contact.position, VectorUtils.getAngle(targetData.hitNormal))
    }
  }

  function drawCrosshair(position: Position, angle: number) {
    drawerBase.drawSegment(ctx.value!, new Segment(
        BattleUtils.shiftedPosition(position, CROSSHAIR_RADIUS_Y, angle),
        BattleUtils.shiftedPosition(position, -CROSSHAIR_RADIUS_Y, angle),
    ), 1, CROSSHAIR_COLOR)
    drawerBase.drawSegment(ctx.value!, new Segment(
        BattleUtils.shiftedPosition(position, CROSSHAIR_RADIUS_X, angle - Math.PI / 2),
        BattleUtils.shiftedPosition(position, -CROSSHAIR_RADIUS_X, angle - Math.PI / 2),
    ), 1, CROSSHAIR_COLOR)
    const center = drawerBase.transformPosition(position)
    const radiusX = drawerBase.scale(CROSSHAIR_RADIUS_X)
    const radiusY = drawerBase.scale(CROSSHAIR_RADIUS_Y)
    ctx.value!.strokeStyle = CROSSHAIR_COLOR
    ctx.value!.beginPath()
    ctx.value!.ellipse(center.x, center.y, radiusX, radiusY, 3 * Math.PI / 2 - angle, 0, 2 * Math.PI)
    ctx.value!.stroke()
    ctx.value!.closePath()
  }

  return {draw}
}
