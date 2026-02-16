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
import {Circle, HalfCircle, Segment, Trapeze} from "~/playground/data/geometry";
import {BattleType} from "~/playground/data/battle";
import {DefaultColors} from "~/dictionary/default-colors";
import {mdiAccount, mdiRobot} from "@mdi/js";

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

      const wheelRadius = vehicleModel.specs.wheelRadius

      drawTurret(vehicleModel)
      drawWheels(vehicleModel, wheelRadius)
      drawSmallWheels(vehicleModel, wheelRadius)
      drawTrack(vehicleModel)
      drawGun(vehicleModel)
      if (appearances.value[AppearancesNames.HP_ABOVE] === '1') {
        drawHpBar(vehicleModel)
      }
      const nicknameAbove = appearances.value[AppearancesNames.NICKNAMES_ABOVE] === '1'
      if (nicknameAbove) {
        drawNickname(userKey, vehicleModel)
      }
      const teamId = battleStore.battle?.type === BattleType.TEAM_ELIMINATION
          && battleStore.battle?.nicknameTeamMap[userKey]
      if (teamId !== undefined && teamId !== false) {
        drawTeamIcon(userKey, teamId, vehicleModel, nicknameAbove)
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
    drawerBase.drawHalfCircle(new HalfCircle(BodyUtils.getGeometryPosition(vehicleModel),
        turretShape.radius, vehicleModel.state.position.angle))
  }

  function drawTrapezeTurret(vehicleModel: VehicleModel, turretShape: TrapezeShape) {
    drawerBase.drawTrapeze(new Trapeze(BodyUtils.getGeometryBodyPosition(vehicleModel), turretShape))
  }

  function drawWheels(vehicleModel: VehicleModel, wheelRadius: number) {
    drawerBase.drawCircle(new Circle(VehicleUtils.getRightWheelPosition(vehicleModel), wheelRadius))
    drawerBase.drawCircle(new Circle(VehicleUtils.getLeftWheelPosition(vehicleModel), wheelRadius))
  }

  function drawSmallWheels(vehicleModel: VehicleModel, wheelRadius: number) {
    VehicleUtils.getSmallWheels(vehicleModel)
        .forEach((position) => {
          drawerBase.drawCircle(new Circle(position, wheelRadius / 2))
        })
  }

  function drawTrack(vehicleModel: VehicleModel) {
    if (!vehicleModel.state.trackState.broken) {
      drawerBase.setDrawParams({lineWidth: 2})
      drawerBase.drawSegment(new Segment(
          VehicleUtils.getLeftWheelBottomPosition(vehicleModel),
          VehicleUtils.getRightWheelBottomPosition(vehicleModel)
      ))
      drawerBase.drawSegment(new Segment(
          VehicleUtils.getLeftWheelTopPosition(vehicleModel),
          VehicleUtils.getRightWheelTopPosition(vehicleModel)
      ))
    }
  }

  function drawGun(vehicleModel: VehicleModel) {
    drawerBase.drawSegment(new Segment(
        vehicleModel.state.position,
        VehicleUtils.getGunEndPosition(vehicleModel)
    ), {lineWidth: drawerBase.scale(vehicleModel.config.gun.caliber)})
  }

  function drawHpBar(vehicleModel: VehicleModel) {
    ctx.value!.beginPath()
    ctx.value!.lineWidth = 1
    const hpBarHeight = 0.07
    const hpBarWidth = 1.5 * vehicleModel.preCalc.maxRadius
    const hpBarTopLeft = drawerBase.transformPosition({
      x: vehicleModel.state.position.x - hpBarWidth / 2,
      y: vehicleModel.state.position.y + hpBarWidth
    })
    const hpRatio = vehicleModel.state.hitPoints / vehicleModel.specs.hitPoints
    ctx.value!.strokeRect(hpBarTopLeft.x, hpBarTopLeft.y,
        drawerBase.scale(hpBarWidth), drawerBase.scale(hpBarHeight))
    ctx.value!.fillRect(hpBarTopLeft.x, hpBarTopLeft.y,
        drawerBase.scale(hpBarWidth * hpRatio), drawerBase.scale(hpBarHeight))
    ctx.value!.closePath()
  }

  function drawNickname(nickname: string, vehicleModel: VehicleModel) {
    const textHeight = 1.5 * vehicleModel.preCalc.maxRadius
    const position = {
      x: vehicleModel.state.position.x,
      y: vehicleModel.state.position.y + textHeight + 0.1
    }
    drawerBase.drawText({
      position,
      text: restrictNicknameLength(nickname),
      fontSize: 14,
      textAlign: 'center',
    }, {fillStyle: nickname === userStore.user!.nickname ? DefaultColors.SELF_COLOR : 'rgb(256 256 256)'})
  }

  function drawTeamIcon(nickname: string, teamId: number, vehicleModel: VehicleModel, nicknameAbove: boolean) {
    const textHeight = 1.5 * vehicleModel.preCalc.maxRadius
    const position = {
      x: vehicleModel.state.position.x,
      y: vehicleModel.state.position.y + textHeight + (nicknameAbove ? 0.3 : 0.15)
    }
    const playerTeamId = battleStore.battle!.nicknameTeamMap[userStore.user!.nickname]
    const color = playerTeamId === teamId ? DefaultColors.ALLY_TEAM : DefaultColors.ENEMY_TEAM
    const isUser = battleStore.battle?.userNicknames.includes(nickname)
    const icon = isUser ? mdiAccount : mdiRobot
    drawerBase.drawMDIIcon(icon, position, color)
  }

  function restrictNicknameLength(nickname: string) {
    return nickname.substring(0, 12)
  }

  return {draw}
}
