import type {DrawerBase} from '@/playground/composables/drawer/drawer-base'
import type {Ref} from 'vue'
import {useBattleStore} from '~/stores/battle'
import type {BoxModel} from "~/playground/data/model";
import {ShapeNames, type TrapezeShape} from "~/playground/data/shapes";
import {BattleUtils} from "~/playground/utils/battle-utils";
import {type BodyPosition, BoxType, type Position} from "~/playground/data/common";
import {BodyUtils} from "~/playground/utils/body-utils";

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
      const color = box.config.color || 'rgb(256 256 256)'
      if (box.specs.shape.name === ShapeNames.TRAPEZE) {
        const trapeze = box.specs.shape as TrapezeShape
        drawerBase.drawTrapeze(ctx.value, BodyUtils.getGeometryBodyPosition(box), trapeze, 'rgb(256 256 256)')
        if (box.specs.type === BoxType.HP) {
          drawCross(box.state.position, trapeze, color)
          drawAmount(box)
        }
      }
    }
  }

  function drawCross(position: BodyPosition, shape: TrapezeShape, color: string) {
    const angle = position.angle
    const length = shape.height / 4
    const polygon: Position[] = []
    const previous = () => polygon[polygon.length - 1]
    polygon.push(BattleUtils.shiftedPosition(position, Math.sqrt(2) * length / 2, angle - 3 * Math.PI / 4))
    polygon.push(BattleUtils.shiftedPosition(previous(), length, angle - Math.PI / 2))
    polygon.push(BattleUtils.shiftedPosition(previous(), length, angle))
    polygon.push(BattleUtils.shiftedPosition(previous(), length, angle + Math.PI / 2))
    polygon.push(BattleUtils.shiftedPosition(previous(), length, angle))
    polygon.push(BattleUtils.shiftedPosition(previous(), length, angle + Math.PI / 2))
    polygon.push(BattleUtils.shiftedPosition(previous(), length, angle - Math.PI))
    polygon.push(BattleUtils.shiftedPosition(previous(), length, angle + Math.PI / 2))
    polygon.push(BattleUtils.shiftedPosition(previous(), length, angle - Math.PI))
    polygon.push(BattleUtils.shiftedPosition(previous(), length, angle - Math.PI / 2))
    polygon.push(BattleUtils.shiftedPosition(previous(), length, angle - Math.PI))
    polygon.push(BattleUtils.shiftedPosition(previous(), length, angle - Math.PI / 2))
    drawerBase.drawPolygon(ctx.value, polygon, color)
  }

  function drawAmount(box: BoxModel) {
    ctx.value!.beginPath()
    ctx.value!.fillStyle = 'rgb(256,256,256)'
    ctx.value!.font = drawerBase.getFont(12)
    ctx.value!.lineWidth = 1
    const amountWidth = 1.5 * box.preCalc.maxRadius
    const amountPosition = drawerBase.transformPosition({
      x: box.state.position.x - amountWidth / 2,
      y: box.state.position.y + amountWidth / 2 + 0.1
    })
    ctx.value!.fillText(box.config.amount.toFixed(0) + 'HP', amountPosition.x, amountPosition.y,
        drawerBase.scale(amountWidth))
    ctx.value!.closePath()
  }

  return { draw }
}
