import type {DrawerBase} from '@/playground/composables/drawer/drawer-base'
import type {Ref} from 'vue'
import {useBattleStore} from '~/stores/battle'
import {VehicleUtils} from '@/playground/utils/vehicle-utils'
import type {BattleModel, VehicleModel} from "~/playground/data/model";
import {useUserStore} from "~/stores/user";
import {BattleUtils} from "~/playground/utils/battle-utils";
import {useSettingsStore} from "~/stores/settings";
import {Segment} from "~/playground/data/geometry";
import {type Position} from "~/playground/data/common";
import {VectorUtils} from "~/playground/utils/vector-utils";
import {TrajectoryContactUtils} from "~/playground/utils/trajectory-contact-utils";
import {VehicleCalculations} from "~/playground/data/calculations";

const CROSSHAIR_RADIUS_X = 0.3
const CROSSHAIR_RADIUS_Y = 0.1
const CROSSHAIR_COLOR = 'rgb(256,256,256)'

export function useShellTrajectoryDrawer(
    drawerBase: DrawerBase,
    ctx: Ref<CanvasRenderingContext2D | undefined>
) {
  const battleStore = useBattleStore()
  const userStore = useUserStore()
  const settingsStore = useSettingsStore()

  function draw() {
    if (settingsStore.settings?.debug && settingsStore.settings?.showShellTrajectory && battleStore.vehicles) {
      Object.keys(battleStore.vehicles)
          .filter(key => key === userStore.user!.nickname)
          .forEach(key => drawTrajectory(battleStore.vehicles![key]!, battleStore.battle!.model))
    }
  }

  function drawTrajectory(vehicleModel: VehicleModel, battleModel: BattleModel) {
    const selectedShell = vehicleModel.state.gunState.selectedShell
    if (!selectedShell) {
      return
    }
    const velocityMagnitude = vehicleModel.config.gun.availableShells[selectedShell]!.velocity
    const startPosition = VehicleUtils.getGunEndPosition(vehicleModel)
    const angle = vehicleModel.state.position.angle + vehicleModel.state.gunState.angle
    const directionSign = Math.sign(Math.cos(angle))
    const maxX = BattleUtils.getRoomWidth(battleModel.room.specs)
    let x = startPosition.x
    let y = startPosition.y
    ctx.value!.strokeStyle = 'rgb(150,150,150)'
    ctx.value!.lineWidth = 1
    ctx.value!.beginPath()
    let previous: Position = {x, y}
    let pos = drawerBase.transformPosition({x, y})
    ctx.value!.moveTo(pos.x, pos.y)
    const step = 0.1
    const vehicles: VehicleCalculations[] = Object.values(battleModel.vehicles)
        .map((vehicle: VehicleModel) => new VehicleCalculations(vehicle))
    let target: Position | null = null
    while (x > 0 && x < maxX && y > 0) {
      x += step * directionSign
      const x1 = x - startPosition.x
      y = startPosition.y + x1 * Math.tan(angle) - battleModel.room.specs.gravityAcceleration * x1 * x1
          / (2 * velocityMagnitude * velocityMagnitude * Math.cos(angle) * Math.cos(angle))
      const trajectory = new Segment(previous, {x, y})
      target = BattleUtils.getFirstPointUnderGround(trajectory, battleModel.room)
      if (!target) {
        for (const vehicle of vehicles) {
          const cnt = TrajectoryContactUtils.detectWithVehicle(trajectory, vehicle)
          if (cnt) {
            target = cnt.contact.position
            break
          }
          const rwc = TrajectoryContactUtils.detectWithWheel(trajectory, vehicle.rightWheel)
          if (rwc) {
            target = rwc.position
            break
          }
          const lwc = TrajectoryContactUtils.detectWithWheel(trajectory, vehicle.leftWheel)
          if (lwc) {
            target = lwc.position
            break
          }
        }
      }
      if (target) {
        pos = drawerBase.transformPosition(target)
        ctx.value!.lineTo(pos.x, pos.y)
        break
      }
      pos = drawerBase.transformPosition({x, y})
      ctx.value!.lineTo(pos.x, pos.y)
      previous = {x, y}
    }
    ctx.value!.stroke()
    ctx.value!.closePath()
    if (target) {
      drawCrosshair(target, VectorUtils.angleFromTo(previous, {x, y}))
    }
  }

  function drawCrosshair(position: Position, angle: number) {
    drawerBase.drawSegment(ctx.value!, new Segment(
        BattleUtils.shiftedPosition(position, CROSSHAIR_RADIUS_Y, angle),
        BattleUtils.shiftedPosition(position, -CROSSHAIR_RADIUS_Y, angle),
    ), 1, CROSSHAIR_COLOR)
    drawerBase.drawSegment(ctx.value!, new Segment(
        BattleUtils.shiftedPosition(position, CROSSHAIR_RADIUS_X, angle - Math.PI / 2),
        BattleUtils.shiftedPosition(position, -CROSSHAIR_RADIUS_X, angle - Math.PI / 2),
    ), 1, CROSSHAIR_COLOR)
    const center = drawerBase.transformPosition(position)
    const radiusX = drawerBase.scale(CROSSHAIR_RADIUS_X)
    const radiusY = drawerBase.scale(CROSSHAIR_RADIUS_Y)
    ctx.value!.strokeStyle = CROSSHAIR_COLOR
    ctx.value!.beginPath()
    ctx.value!.ellipse(center.x, center.y, radiusX, radiusY, 3 * Math.PI / 2 - angle, 0, 2 * Math.PI)
    ctx.value!.stroke()
    ctx.value!.closePath()
  }

  return {draw}
}
