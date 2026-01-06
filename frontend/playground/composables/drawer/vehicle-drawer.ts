import type {DrawerBase} from '@/playground/composables/drawer/drawer-base'
import type {Ref} from 'vue'
import {useBattleStore} from '~/stores/battle'
import {VehicleUtils} from '@/playground/utils/vehicle-utils'
import type {VehicleModel} from "~/playground/data/model";
import {useUserSettingsStore} from "~/stores/user-settings";
import {AppearancesNames} from "~/dictionary/appearances-names";
import {type HalfCircleShape, ShapeNames, type TrapezeShape} from "~/playground/data/shapes";
import {BodyUtils} from "~/playground/utils/body-utils";
import {useUserStore} from "~/stores/user";
import {Trapeze} from "~/playground/data/geometry";

export function useVehicleDrawer(
    drawerBase: DrawerBase,
    ctx: Ref<CanvasRenderingContext2D | undefined>
) {
  const battleStore = useBattleStore()
  const userStore = useUserStore()
  const userSettingsStore = useUserSettingsStore()

  const appearances = computed(() => userSettingsStore.appearancesOrDefaultsNameValueMapping)

  function draw() {
    if (battleStore.vehicles) {
      Object.keys(battleStore.vehicles).forEach(drawVehicle)
    }
  }

  function drawVehicle(userKey: string) {
    const vehicleModel = battleStore.vehicles![userKey]!
    const color = VehicleUtils.getColor(userKey, userStore.user!.nickname, vehicleModel)
    if (ctx.value) {
      ctx.value.fillStyle = color
      ctx.value.strokeStyle = color

      const wheelRadius = drawerBase.scale(vehicleModel.specs.wheelRadius)

      drawTurret(vehicleModel)
      drawWheels(vehicleModel, wheelRadius)
      drawSmallWheels(vehicleModel, wheelRadius)
      drawTrack(vehicleModel)
      drawGun(vehicleModel)
      if (appearances.value[AppearancesNames.HP_ABOVE] === '1') {
        drawHpBar(vehicleModel)
      }
      if (appearances.value[AppearancesNames.NICKNAMES_ABOVE] === '1') {
        drawNickname(userKey, vehicleModel)
      }
    }
  }

  function drawTurret(vehicleModel: VehicleModel) {
    const turretShape = vehicleModel.specs.turretShape
    switch (turretShape.name) {
      case ShapeNames.HALF_CIRCLE:
        drawHalfCircleTurret(vehicleModel, turretShape as HalfCircleShape)
        break
      case ShapeNames.TRAPEZE:
        drawTrapezeTurret(vehicleModel, turretShape as TrapezeShape)
        break
    }
  }

  function drawHalfCircleTurret(vehicleModel: VehicleModel, turretShape: HalfCircleShape) {
    const position = drawerBase.transformPosition(BodyUtils.getGeometryPosition(vehicleModel))
    const startAngle = Math.PI - vehicleModel.state.position.angle
    const endAngle = 2 * Math.PI - vehicleModel.state.position.angle
    const radius = drawerBase.scale(turretShape.radius)
    ctx.value!.beginPath()
    ctx.value!.arc(position.x, position.y, radius, startAngle, endAngle)
    ctx.value!.fill()
    ctx.value!.closePath()
  }

  function drawTrapezeTurret(vehicleModel: VehicleModel, turretShape: TrapezeShape) {
    drawerBase.drawTrapeze(new Trapeze(BodyUtils.getGeometryBodyPosition(vehicleModel), turretShape))
  }

  function drawWheels(vehicleModel: VehicleModel, wheelRadius: number) {
    ctx.value!.beginPath()
    const rightWheelPosition = drawerBase.transformPosition(
        VehicleUtils.getRightWheelPosition(vehicleModel)
    )
    const leftWheelPosition = drawerBase.transformPosition(
        VehicleUtils.getLeftWheelPosition(vehicleModel)
    )
    ctx.value!.arc(rightWheelPosition.x, rightWheelPosition.y, wheelRadius, 0, 2 * Math.PI)
    ctx.value!.fill()
    ctx.value!.arc(leftWheelPosition.x, leftWheelPosition.y, wheelRadius, 0, 2 * Math.PI)
    ctx.value!.fill()
    ctx.value!.closePath()
  }

  function drawSmallWheels(vehicleModel: VehicleModel, wheelRadius: number) {
    VehicleUtils.getSmallWheels(vehicleModel)
        .map(drawerBase.transformPosition)
        .forEach((position) => {
          ctx.value!.beginPath()
          ctx.value!.arc(position.x, position.y, wheelRadius / 2, 0, 2 * Math.PI)
          ctx.value!.fill()
          ctx.value!.closePath()
        })
  }

  function drawTrack(vehicleModel: VehicleModel) {
    if (!vehicleModel.state.trackState.broken) {
      ctx.value!.beginPath()
      ctx.value!.lineWidth = 2
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
      ctx.value!.moveTo(bottomTrackBeginPosition.x, bottomTrackBeginPosition.y)
      ctx.value!.lineTo(bottomTrackEndPosition.x, bottomTrackEndPosition.y)
      ctx.value!.stroke()
      ctx.value!.moveTo(topTrackBeginPosition.x, topTrackBeginPosition.y)
      ctx.value!.lineTo(topTrackEndPosition.x, topTrackEndPosition.y)
      ctx.value!.stroke()
      ctx.value!.closePath()
    }
  }

  function drawGun(vehicleModel: VehicleModel) {
    const gunBeginPosition = drawerBase.transformPosition(
        vehicleModel.state.position
    )
    const gunEndPosition = drawerBase.transformPosition(
        VehicleUtils.getGunEndPosition(vehicleModel)
    )
    ctx.value!.beginPath()
    ctx.value!.lineWidth = drawerBase.scale(vehicleModel.config.gun.caliber)
    ctx.value!.moveTo(gunBeginPosition.x, gunBeginPosition.y)
    ctx.value!.lineTo(gunEndPosition.x, gunEndPosition.y)
    ctx.value!.stroke()
    ctx.value!.closePath()
  }

  function drawHpBar(vehicleModel: VehicleModel) {
    ctx.value!.beginPath()
    ctx.value!.lineWidth = 1
    const hpBarHeight = 0.07
    const hpBarWidth = 1.5 * vehicleModel.preCalc.maxRadius
    const hpBarTopLeft = drawerBase.transformPosition({
      x: vehicleModel.state.position.x - hpBarWidth / 2,
      y: vehicleModel.state.position.y + hpBarWidth + hpBarHeight
    })
    const hpRatio = vehicleModel.state.hitPoints / vehicleModel.specs.hitPoints
    ctx.value!.strokeRect(hpBarTopLeft.x, hpBarTopLeft.y,
        drawerBase.scale(hpBarWidth), drawerBase.scale(hpBarHeight))
    ctx.value!.fillRect(hpBarTopLeft.x, hpBarTopLeft.y,
        drawerBase.scale(hpBarWidth * hpRatio), drawerBase.scale(hpBarHeight))
    ctx.value!.closePath()
  }

  function drawNickname(userKey: string, vehicleModel: VehicleModel) {
    const textWidth = 1.5 * vehicleModel.preCalc.maxRadius
    const position = {
      x: vehicleModel.state.position.x - textWidth / 2,
      y: vehicleModel.state.position.y + textWidth + 0.1
    }
    drawerBase.drawText({
      position,
      text: restrictNicknameLength(userKey),
      fontSize: 16,
      textWidth
    }, {fillStyle: 'rgb(256 256 256)'})
  }

  function restrictNicknameLength(nickname: string) {
    return nickname.substring(0, 8)
  }

  return {draw}
}
