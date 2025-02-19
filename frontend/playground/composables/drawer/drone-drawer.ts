import type { DrawerBase } from '@/playground/composables/drawer/drawer-base'
import type { Ref } from 'vue'
import { useBattleStore } from '~/stores/battle'
import type {DroneModel} from '@/playground/data/model'
import {BattleUtils} from "~/playground/utils/battle-utils";

export function useDroneDrawer(
  drawerBase: DrawerBase,
  ctx: Ref<CanvasRenderingContext2D | undefined>
) {
  const battleStore = useBattleStore()

  function draw() {
    if (battleStore.drones) {
      Object.values(battleStore.drones).forEach(drawDrone)
    }
  }

  function drawDrone(droneModel: DroneModel) {
    if (ctx.value) {
      ctx.value.fillStyle = 'rgb(256 256 256)'
      ctx.value.strokeStyle = 'rgb(256 256 256)'
      ctx.value.lineWidth = 1
      ctx.value.beginPath()
      const enginesRadius = droneModel.specs.enginesRadius
      const hullRadius = droneModel.specs.hullRadius
      const rawPosition = droneModel.state.position
      const rawRightWingDownPosition = BattleUtils.shiftedPosition(rawPosition, enginesRadius, rawPosition.angle)
      const rawLeftWingDownPosition = BattleUtils.shiftedPosition(rawPosition, enginesRadius, rawPosition.angle - Math.PI)
      const rawRightWingUpPosition = BattleUtils.shiftedPosition(rawRightWingDownPosition, hullRadius, rawPosition.angle + Math.PI / 2)
      const rawLeftWingUpPosition = BattleUtils.shiftedPosition(rawLeftWingDownPosition, hullRadius, rawPosition.angle + Math.PI / 2)
      const position = drawerBase.transformPosition(droneModel.state.position)
      const rightWingDownPosition = drawerBase.transformPosition(rawRightWingDownPosition)
      const leftWingDownPosition = drawerBase.transformPosition(rawLeftWingDownPosition)
      const rightWingUpPosition = drawerBase.transformPosition(rawRightWingUpPosition)
      const leftWingUpPosition = drawerBase.transformPosition(rawLeftWingUpPosition)
      ctx.value.arc(position.x, position.y, drawerBase.scale(hullRadius), 0, 2 * Math.PI)
      ctx.value.fill()

      ctx.value.lineWidth = 2
      ctx.value.moveTo(leftWingUpPosition.x, leftWingUpPosition.y)
      ctx.value.lineTo(leftWingDownPosition.x, leftWingDownPosition.y)
      ctx.value.lineTo(rightWingDownPosition.x, rightWingDownPosition.y)
      ctx.value.lineTo(rightWingUpPosition.x, rightWingUpPosition.y)
      ctx.value.stroke()

      ctx.value.closePath()
    }
  }

  return { draw }
}
