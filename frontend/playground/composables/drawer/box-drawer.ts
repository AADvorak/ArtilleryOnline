import type {DrawerBase} from '@/playground/composables/drawer/drawer-base'
import type {Ref} from 'vue'
import {useBattleStore} from '~/stores/battle'
import type {BoxModel} from "~/playground/data/model";
import {ShapeNames, type TrapezeShape} from "~/playground/data/shapes";
import {BattleUtils} from "~/playground/utils/battle-utils";
import type {BodyPosition} from "~/playground/data/common";

export function useBoxDrawer(
  drawerBase: DrawerBase,
  ctx: Ref<CanvasRenderingContext2D | undefined>
) {
  const battleStore = useBattleStore()

  function draw() {
    const boxes = battleStore.battle?.model.boxes
    if (boxes) {
      Object.values(boxes).forEach(drawBox)
    }
  }

  function drawBox(box: BoxModel) {
    if (ctx.value) {
      ctx.value.fillStyle = 'rgb(256 256 256)'
      ctx.value.lineWidth = 1
      if (box.specs.shape.name === ShapeNames.TRAPEZE) {
        drawTrapezeBox(box.state.position, box.specs.shape as TrapezeShape)
      }
    }
  }

  function drawTrapezeBox(position: BodyPosition, shape: TrapezeShape) {
    const angle = position.angle
    const bottomCenter = BattleUtils.shiftedPosition(position, shape.height / 2, angle - Math.PI / 2)
    const topCenter = BattleUtils.shiftedPosition(position, shape.height / 2, angle + Math.PI / 2)

    const bottomRight = drawerBase.transformPosition(
        BattleUtils.shiftedPosition(bottomCenter, shape.bottomRadius, angle))
    const bottomLeft = drawerBase.transformPosition(
        BattleUtils.shiftedPosition(bottomCenter, -shape.bottomRadius, angle))
    const topRight = drawerBase.transformPosition(
        BattleUtils.shiftedPosition(topCenter, shape.topRadius, angle))
    const topLeft = drawerBase.transformPosition(
        BattleUtils.shiftedPosition(topCenter, -shape.topRadius, angle))

    ctx.value!.beginPath()
    ctx.value!.moveTo(bottomLeft.x, bottomLeft.y)
    ctx.value!.lineTo(bottomRight.x, bottomRight.y)
    ctx.value!.lineTo(topRight.x, topRight.y)
    ctx.value!.lineTo(topLeft.x, topLeft.y)
    ctx.value!.fill()
    ctx.value!.closePath()
  }

  return { draw }
}
