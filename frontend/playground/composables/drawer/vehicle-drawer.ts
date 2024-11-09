import type {DrawerBase} from '@/playground/composables/drawer/drawer-base'
import type {Ref} from 'vue'
import {useBattleStore} from '~/stores/battle'
import {VehicleUtils} from '@/playground/utils/vehicle-utils'
import type {VehicleModel} from "~/playground/data/model";
import type {Position} from "~/playground/data/common";
import {useUserSettingsStore} from "~/stores/user-settings";
import {AppearancesNames} from "~/dictionary/appearances-names";

export function useVehicleDrawer(
    drawerBase: DrawerBase,
    ctx: Ref<CanvasRenderingContext2D>
) {
  const battleStore = useBattleStore()
  const userSettingsStore = useUserSettingsStore()

  const appearances = computed(() => userSettingsStore.appearancesOrDefaultsNameValueMapping)

  function draw() {
    if (battleStore.vehicles) {
      Object.keys(battleStore.vehicles).forEach(drawVehicle)
    }
  }

  function drawVehicle(userKey: string) {
    const vehicleModel = battleStore.vehicles[userKey]
    const color = VehicleUtils.getColor(userKey, vehicleModel)
    if (ctx.value) {
      ctx.value.fillStyle = color
      ctx.value.strokeStyle = color

      const position = drawerBase.transformPosition(vehicleModel.state.position)
      const radius = drawerBase.scale(vehicleModel.specs.radius)
      const wheelRadius = drawerBase.scale(vehicleModel.specs.wheelRadius)

      drawHull(vehicleModel, position, radius)
      drawWheels(vehicleModel, wheelRadius)
      drawSmallWheels(vehicleModel, wheelRadius)
      drawTrack(vehicleModel)
      drawGun(vehicleModel, position)
      if (appearances.value[AppearancesNames.HP_ABOVE] === '1') {
        drawHpBar(vehicleModel)
      }
      if (appearances.value[AppearancesNames.NICKNAMES_ABOVE] === '1') {
        drawNickname(userKey, vehicleModel)
      }
    }
  }

  function drawHull(vehicleModel: VehicleModel, position: Position, radius: number) {
    const startAngle = Math.PI - vehicleModel.state.angle
    const endAngle = 2 * Math.PI - vehicleModel.state.angle
    ctx.value.beginPath()
    ctx.value.arc(position.x, position.y, radius, startAngle, endAngle)
    ctx.value.fill()
    ctx.value.closePath()
  }

  function drawWheels(vehicleModel: VehicleModel, wheelRadius: number) {
    ctx.value.beginPath()
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
  }

  function drawSmallWheels(vehicleModel: VehicleModel, wheelRadius: number) {
    VehicleUtils.getSmallWheels(vehicleModel)
        .map(drawerBase.transformPosition)
        .forEach((position) => {
          ctx.value.beginPath()
          ctx.value.arc(position.x, position.y, wheelRadius / 2, 0, 2 * Math.PI)
          ctx.value.fill()
          ctx.value.closePath()
        })
  }

  function drawTrack(vehicleModel: VehicleModel) {
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
  }

  function drawGun(vehicleModel: VehicleModel, position: Position) {
    const gunEndPosition = drawerBase.transformPosition(
        VehicleUtils.getGunEndPosition(vehicleModel)
    )
    ctx.value.beginPath()
    ctx.value.lineWidth = drawerBase.scale(vehicleModel.config.gun.caliber)
    ctx.value.moveTo(position.x, position.y)
    ctx.value.lineTo(gunEndPosition.x, gunEndPosition.y)
    ctx.value.stroke()
    ctx.value.closePath()
  }

  function drawHpBar(vehicleModel: VehicleModel) {
    ctx.value.beginPath()
    ctx.value.lineWidth = 1
    const hpBarHeight = 0.07
    const hpBarTopLeft = drawerBase.transformPosition({
      x: vehicleModel.state.position.x - vehicleModel.specs.radius,
      y: vehicleModel.state.position.y + 2 * vehicleModel.specs.radius + hpBarHeight
    })
    const hpRatio = vehicleModel.state.hitPoints / vehicleModel.specs.hitPoints
    ctx.value.strokeRect(hpBarTopLeft.x, hpBarTopLeft.y,
        drawerBase.scale(2 * vehicleModel.specs.radius), drawerBase.scale(hpBarHeight))
    ctx.value.fillRect(hpBarTopLeft.x, hpBarTopLeft.y,
        drawerBase.scale(2 * vehicleModel.specs.radius * hpRatio), drawerBase.scale(hpBarHeight))
    ctx.value.closePath()
  }

  function drawNickname(userKey: string, vehicleModel: VehicleModel) {
    ctx.value.beginPath()
    ctx.value.fillStyle = 'rgb(256,256,256)'
    ctx.value.font = '16px arial'
    ctx.value.lineWidth = 1
    const nicknamePosition = drawerBase.transformPosition({
      x: vehicleModel.state.position.x - vehicleModel.specs.radius,
      y: vehicleModel.state.position.y + 2 * vehicleModel.specs.radius + 0.1
    })
    ctx.value.fillText(restrictNicknameLength(userKey), nicknamePosition.x, nicknamePosition.y,
        drawerBase.scale(2 * vehicleModel.specs.radius))
    ctx.value.closePath()
  }

  function restrictNicknameLength(nickname: string) {
    return nickname.substring(0, 8)
  }

  return {draw}
}
