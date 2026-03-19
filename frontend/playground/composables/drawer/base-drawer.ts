import type {DrawerBase} from '@/playground/composables/drawer/drawer-base'
import type {Ref} from 'vue'
import {useBattleStore} from '~/stores/battle'
import type {BaseModel} from '@/playground/data/model'
import {BattleUtils} from "~/playground/utils/battle-utils";
import {ShapeNames, type TrapezeShape} from "~/playground/data/shapes";
import type {BodyPosition, Position} from "~/playground/data/common";
import {Trapeze} from "~/playground/data/geometry";
import type {CapturePoints} from "~/playground/data/state";
import {useUserStore} from "~/stores/user";
import {DefaultColors} from "~/dictionary/default-colors";

export function useBaseDrawer(
  drawerBase: DrawerBase,
  ctx: Ref<CanvasRenderingContext2D | undefined>
) {
  const battleStore = useBattleStore()
  const userStore = useUserStore()

  function draw() {
    const bases = battleStore.battle?.model.bases
    if (bases) {
      Object.values(bases).forEach(drawBase)
    }
  }

  function drawBase(baseModel: BaseModel) {
    const roomModel = battleStore.battle!.model.room
    const centerBottom: BodyPosition = {
      ...BattleUtils.getNearestGroundPosition(baseModel.config.positionX, roomModel),
      angle: 0
    }
    const color = 'white'
    const flagpoleHeight = baseModel.specs.radius * 1.5
    const flagpoleShape: TrapezeShape = {
      name: ShapeNames.TRAPEZE,
      height: flagpoleHeight,
      bottomRadius: 0.01,
      topRadius: 0.01
    }
    const flagHeight = 0.3
    const flagWidth = 0.5
    const flagShape: TrapezeShape = {
      name: ShapeNames.TRAPEZE,
      height: flagHeight,
      bottomRadius: flagWidth / 2,
      topRadius: flagWidth / 2
    }
    const flagPosition: BodyPosition = {
      x: centerBottom.x + flagWidth / 2,
      y: centerBottom.y + flagpoleHeight - flagHeight,
      angle: 0
    }
    drawerBase.drawTrapeze(new Trapeze(centerBottom, flagpoleShape), {fillStyle: color})
    drawerBase.drawTrapeze(new Trapeze(flagPosition, flagShape), {fillStyle: color})
    //drawEllipse(centerBottom, baseModel.specs.radius, color)
    drawGround(centerBottom.x, baseModel.specs.radius, color)
    drawCaptureBar(baseModel, centerBottom)
  }

  function drawEllipse(centerBottom: Position, radius: number, color: string) {
    const radiusX = drawerBase.scale(radius)
    const centerTransformed = drawerBase.transformPosition(centerBottom)
    ctx.value!.beginPath()
    ctx.value!.strokeStyle = color
    ctx.value!.ellipse(centerTransformed.x, centerTransformed.y, radiusX, radiusX / 5, 0, 0, 2 * Math.PI)
    ctx.value!.stroke()
    ctx.value!.closePath()
  }

  function drawGround(x: number, radius: number, color: string) {
    const groundPoints = BattleUtils.getGroundPointsBetween(x - radius,
        x + radius, battleStore.battle!.model.room)
        .map(point => ({x: point.x, y: point.y + 0.03}))
    drawerBase.drawPolygon(groundPoints, {stroke: true, strokeStyle: color, lineWidth: 4})
  }

  function drawCaptureBar(baseModel: BaseModel, position: Position) {
    const sumCapturePoints = Math.min(
        getSumCapturePoints(baseModel.state.capturePoints),
        baseModel.specs.capturePoints
    )
    if (sumCapturePoints > 0) {
      const color = getProgressColor(baseModel)
      ctx.value!.beginPath()
      ctx.value!.lineWidth = 1
      ctx.value!.strokeStyle = color
      ctx.value!.fillStyle = color
      const barHeight = 0.07
      const barWidth = 1.5 * baseModel.specs.radius
      const barTopLeft = drawerBase.transformPosition({
        x: position.x - barWidth / 2,
        y: position.y + barWidth + baseModel.specs.radius * 1.5
      })
      const captureRatio = sumCapturePoints / baseModel.specs.capturePoints
      ctx.value!.strokeRect(barTopLeft.x, barTopLeft.y,
          drawerBase.scale(barWidth), drawerBase.scale(barHeight))
      ctx.value!.fillRect(barTopLeft.x, barTopLeft.y,
          drawerBase.scale(barWidth * captureRatio), drawerBase.scale(barHeight))
      ctx.value!.closePath()
    }
  }

  function getProgressColor(baseModel: BaseModel) {
    if (baseModel.state.captureBlocked) {
      return DefaultColors.PROGRESS_NOT_READY
    }
    const playerTeamId = battleStore.battle!.nicknameTeamMap[userStore.user!.nickname]
    return  playerTeamId === baseModel.state.capturingTeamId
        ? DefaultColors.ALLY_TEAM : DefaultColors.ENEMY_TEAM
  }

  function getSumCapturePoints(capturePoints: CapturePoints) {
    return Object.values(capturePoints).reduce((a, c) => a + c, 0)
  }

  return { draw }
}
