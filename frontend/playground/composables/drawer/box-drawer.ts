import type {DrawerBase} from '@/playground/composables/drawer/drawer-base'
import type {Ref} from 'vue'
import {useBattleStore} from '~/stores/battle'
import type {BoxModel} from "~/playground/data/model";
import {ShapeNames, type TrapezeShape} from "~/playground/data/shapes";
import {BattleUtils} from "~/playground/utils/battle-utils";
import {type BodyPosition, BoxType, type Position} from "~/playground/data/common";

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
        drawerBase.drawTrapeze(ctx.value, getBottomCenter(box), trapeze, 'rgb(256 256 256)')
        if (box.specs.type === BoxType.HP) {
          drawCross(box.state.position, trapeze, color)
        }
      }
    }
  }

  function getBottomCenter(box: BoxModel) {
    const position = box.state.position
    const comShift = box.preCalc.centerOfMassShift
    const bcPosition = BattleUtils.shiftedPosition(position, - comShift.distance, position.angle + comShift.angle)
    return {
      x: bcPosition.x,
      y: bcPosition.y,
      angle: position.angle
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

  return { draw }
}
