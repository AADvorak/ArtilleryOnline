import type { DrawerBase } from '@/playground/composables/drawer/drawer-base'
import type { Ref } from 'vue'
import { useBattleStore } from '@/playground/stores/battle'
import { VehicleUtils } from '@/playground/utils/vehicle-utils'
import { useUserKeyStore } from '@/playground/stores/user-key'

export function useVehicleDrawer(
  drawerBase: DrawerBase,
  ctx: Ref<CanvasRenderingContext2D | undefined>
) {
  const battleStore = useBattleStore()
  const userStore = useUserKeyStore()

  function draw() {
    if (battleStore.vehicles) {
      Object.keys(battleStore.vehicles).forEach(drawVehicle)
    }
  }

  function drawVehicle(userKey: string) {
    const vehicleModel = battleStore.vehicles[userKey]
    const color = getColor(userKey)
    if (ctx.value) {
      ctx.value.fillStyle = color
      ctx.value.strokeStyle = color
      ctx.value.beginPath()
      const position = drawerBase.transformPosition(vehicleModel.state.position)
      const gunEndPosition = drawerBase.transformPosition(
        VehicleUtils.getGunEndPosition(vehicleModel)
      )
      const radius = drawerBase.scale(vehicleModel.specs.radius)
      const startAngle = Math.PI - vehicleModel.state.angle
      const endAngle = 2 * Math.PI - vehicleModel.state.angle
      ctx.value.arc(position.x, position.y, radius, startAngle, endAngle)
      ctx.value.fill()
      ctx.value.closePath()

      ctx.value.beginPath()
      const wheelRadius = drawerBase.scale(vehicleModel.specs.wheelRadius)
      const rightWheelPosition = drawerBase.transformPosition(
        VehicleUtils.getRightWheelPosition(vehicleModel)
      )
      const leftWheelPosition = drawerBase.transformPosition(
        VehicleUtils.getLeftWheelPosition(vehicleModel)
      )
      ctx.value.arc(rightWheelPosition.x, rightWheelPosition.y, wheelRadius, 0, 2 * Math.PI)
      ctx.value.fill()
      ctx.value.arc(leftWheelPosition.x, leftWheelPosition.y, wheelRadius, 0, 2 * Math.PI)
      ctx.value.fill()
      ctx.value.closePath()

      VehicleUtils.getSmallWheels(vehicleModel)
        .map(drawerBase.transformPosition)
        .forEach((position) => {
          ctx.value.beginPath()
          ctx.value.arc(position.x, position.y, wheelRadius / 2, 0, 2 * Math.PI)
          ctx.value.fill()
          ctx.value.closePath()
        })

      if (!vehicleModel.state.trackState.broken) {
        ctx.value.beginPath()
        ctx.value.lineWidth = 2
        const bottomTrackBeginPosition = drawerBase.transformPosition(
          VehicleUtils.getLeftWheelBottomPosition(vehicleModel)
        )
        const bottomTrackEndPosition = drawerBase.transformPosition(
          VehicleUtils.getRightWheelBottomPosition(vehicleModel)
        )
        const topTrackBeginPosition = drawerBase.transformPosition(
          VehicleUtils.getLeftWheelTopPosition(vehicleModel)
        )
        const topTrackEndPosition = drawerBase.transformPosition(
          VehicleUtils.getRightWheelTopPosition(vehicleModel)
        )
        ctx.value.moveTo(bottomTrackBeginPosition.x, bottomTrackBeginPosition.y)
        ctx.value.lineTo(bottomTrackEndPosition.x, bottomTrackEndPosition.y)
        ctx.value.stroke()
        ctx.value.moveTo(topTrackBeginPosition.x, topTrackBeginPosition.y)
        ctx.value.lineTo(topTrackEndPosition.x, topTrackEndPosition.y)
        ctx.value.stroke()
        ctx.value.closePath()
      }

      ctx.value.beginPath()
      ctx.value.lineWidth = 4
      ctx.value.moveTo(position.x, position.y)
      ctx.value.lineTo(gunEndPosition.x, gunEndPosition.y)
      ctx.value.stroke()
      ctx.value.closePath()
      ctx.value.lineWidth = 1
    }
  }

  function getColor(userKey: string) {
    return userKey === userStore.userKey ? 'rgb(60,200,0)' : 'rgb(200 0 0)'
  }

  return { draw }
}
