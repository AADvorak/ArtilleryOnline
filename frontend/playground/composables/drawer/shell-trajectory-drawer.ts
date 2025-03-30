import type {DrawerBase} from '@/playground/composables/drawer/drawer-base'
import type {Ref} from 'vue'
import {useBattleStore} from '~/stores/battle'
import {VehicleUtils} from '@/playground/utils/vehicle-utils'
import type {VehicleModel} from "~/playground/data/model";
import {useUserStore} from "~/stores/user";
import {BattleUtils} from "~/playground/utils/battle-utils";
import type {RoomSpecs} from "~/playground/data/specs";

export function useShellTrajectoryDrawer(
    drawerBase: DrawerBase,
    ctx: Ref<CanvasRenderingContext2D | undefined>
) {
  const battleStore = useBattleStore()
  const userStore = useUserStore()

  function draw() {
    if (battleStore.vehicles) {
      const roomSpecs = battleStore.battle!.model.room.specs
      Object.keys(battleStore.vehicles)
          .filter(key => key === userStore.user!.nickname)
          .forEach(key => drawTrajectory(battleStore.vehicles![key], roomSpecs))
    }
  }

  function drawTrajectory(vehicleModel: VehicleModel, roomSpecs: RoomSpecs) {
    const selectedShell = vehicleModel.state.gunState.selectedShell
    if (!selectedShell) {
      return
    }
    const velocityMagnitude = vehicleModel.config.gun.availableShells[selectedShell].velocity
    const startPosition = VehicleUtils.getGunEndPosition(vehicleModel)
    const angle = vehicleModel.state.position.angle + vehicleModel.state.gunState.angle
    const directionSign = Math.sign(Math.cos(angle))
    const maxX = BattleUtils.getRoomWidth(roomSpecs)
    let x = startPosition.x
    let y = startPosition.y
    ctx.value!.strokeStyle = 'rgb(150,150,150)'
    ctx.value!.lineWidth = 1
    ctx.value!.beginPath()
    let pos = drawerBase.transformPosition({x, y})
    ctx.value!.moveTo(pos.x, pos.y)
    const step = 0.1
    while (x > 0 && x < maxX && y > 0) {
      x += step * directionSign
      const x1 = x - startPosition.x
      y = startPosition.y + x1 * Math.tan(angle) - roomSpecs.gravityAcceleration * x1 * x1
          / (2 * velocityMagnitude * velocityMagnitude * Math.cos(angle) * Math.cos(angle))
      pos = drawerBase.transformPosition({x, y})
      ctx.value!.lineTo(pos.x, pos.y)
    }
    ctx.value!.stroke()
    ctx.value!.closePath()
  }

  return {draw}
}
