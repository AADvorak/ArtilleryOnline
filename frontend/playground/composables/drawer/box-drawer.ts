import type {DrawerBase} from '@/playground/composables/drawer/drawer-base'
import {useBattleStore} from '~/stores/battle'
import type {BoxModel} from "~/playground/data/model";
import {ShapeNames, type TrapezeShape} from "~/playground/data/shapes";
import {BattleUtils} from "~/playground/utils/battle-utils";
import {type BodyPosition, BoxType, type Position} from "~/playground/data/common";
import {BodyUtils} from "~/playground/utils/body-utils";
import {Trapeze} from "~/playground/data/geometry";

export function useBoxDrawer(
  drawerBase: DrawerBase
) {
  const battleStore = useBattleStore()

  function draw() {
    const boxes = battleStore.battle?.model.boxes
    if (boxes) {
      Object.values(boxes).forEach(drawBox)
    }
  }

  function drawBox(box: BoxModel) {
    const color = box.config.color || 'rgb(256 256 256)'
    if (box.specs.shape.name === ShapeNames.TRAPEZE) {
      const shape = box.specs.shape as TrapezeShape
      const trapeze = new Trapeze(BodyUtils.getGeometryBodyPosition(box), shape)
      drawerBase.drawTrapeze(trapeze, {fillStyle: 'rgb(256 256 256)'})
      if (box.specs.type === BoxType.HP) {
        drawCross(box.state.position, shape, color)
        drawAmount(box)
      }
      if (box.specs.type === BoxType.AMMO) {
        drawShells(trapeze, color)
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
    drawerBase.drawPolygon(polygon, {fillStyle: color})
  }

  function drawShells(trapeze: Trapeze, color: string) {
    const angle = trapeze.position.angle
    const shellRadius = trapeze.shape.bottomRadius / 6
    const topShellShape: TrapezeShape = {
      name: ShapeNames.TRAPEZE,
      bottomRadius: shellRadius,
      topRadius: 0,
      height: trapeze.shape.height / 3
    }
    const bottomShellShape = {
      name: ShapeNames.TRAPEZE,
      bottomRadius: shellRadius,
      topRadius: shellRadius,
      height: trapeze.shape.height / 2.2
    }
    drawShell(trapeze.position, bottomShellShape, topShellShape, color)
    drawShell({...BattleUtils.shiftedPosition(trapeze.position, 3 * bottomShellShape.bottomRadius, angle), angle},
        bottomShellShape, topShellShape, color)
    drawShell({...BattleUtils.shiftedPosition(trapeze.position, - 3 * bottomShellShape.bottomRadius, angle), angle},
        bottomShellShape, topShellShape, color)
  }

  function drawShell(position: BodyPosition, bottomShape: TrapezeShape, topShape: TrapezeShape, color: string) {
    const angle = position.angle
    const margin = bottomShape.height / 6
    const bottomPosition = BattleUtils.shiftedPosition(position, margin, angle + Math.PI / 2)
    const topPosition = BattleUtils.shiftedPosition(position, 2 * margin + bottomShape.height, angle + Math.PI / 2)
    drawerBase.drawTrapeze(new Trapeze({...bottomPosition, angle}, bottomShape), {fillStyle: color})
    drawerBase.drawTrapeze(new Trapeze({...topPosition, angle}, topShape), {fillStyle: color})
  }

  function drawAmount(box: BoxModel) {
    const textWidth = 1.5 * box.preCalc.maxRadius
    const position = {
      x: box.state.position.x,
      y: box.state.position.y + textWidth / 2 + 0.1
    }
    drawerBase.drawText({
      position,
      text: box.config.amount.toFixed(0) + 'HP',
      fontSize: 14,
      textAlign: 'center',
    }, {fillStyle: 'rgb(256 256 256)'})
  }

  return { draw }
}
