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
        const geometryPosition = BodyUtils.getGeometryBodyPosition(box)
        drawerBase.drawTrapeze(ctx.value, geometryPosition, trapeze, 'rgb(256 256 256)')
        if (box.specs.type === BoxType.HP) {
          drawCross(box.state.position, trapeze, color)
          drawAmount(box)
        }
        if (box.specs.type === BoxType.AMMO) {
          drawShells(geometryPosition, trapeze, color)
        }
      }
    }
  }

  function drawCross(position: BodyPosition, shape: TrapezeShape, color: string) {
    const angle = position.angle
    const length = shape.height / 4
    const polygon: Position[] = []
    const previous = () => polygon[polygon.length - 1]!
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
    drawerBase.drawPolygon(ctx.value!, polygon, color)
  }

  function drawShells(position: BodyPosition, shape: TrapezeShape, color: string) {
    const angle = position.angle
    const shellRadius = shape.bottomRadius / 6
    const topShellShape: TrapezeShape = {
      name: ShapeNames.TRAPEZE,
      bottomRadius: shellRadius,
      topRadius: 0,
      height: shape.height / 3
    }
    const bottomShellShape = {
      name: ShapeNames.TRAPEZE,
      bottomRadius: shellRadius,
      topRadius: shellRadius,
      height: shape.height / 2.2
    }
    drawShell(position, bottomShellShape, topShellShape, color)
    drawShell({...BattleUtils.shiftedPosition(position, 3 * bottomShellShape.bottomRadius, angle), angle},
        bottomShellShape, topShellShape, color)
    drawShell({...BattleUtils.shiftedPosition(position, - 3 * bottomShellShape.bottomRadius, angle), angle},
        bottomShellShape, topShellShape, color)
  }

  function drawShell(position: BodyPosition, bottomShape: TrapezeShape, topShape: TrapezeShape, color: string) {
    const angle = position.angle
    const margin = bottomShape.height / 6
    const bottomPosition = BattleUtils.shiftedPosition(position, margin, angle + Math.PI / 2)
    const topPosition = BattleUtils.shiftedPosition(position, 2 * margin + bottomShape.height, angle + Math.PI / 2)
    drawerBase.drawTrapeze(ctx.value!, {...bottomPosition, angle}, bottomShape, color)
    drawerBase.drawTrapeze(ctx.value!, {...topPosition, angle}, topShape, color)
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
