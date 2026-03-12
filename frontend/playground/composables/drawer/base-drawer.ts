import type {DrawerBase} from '@/playground/composables/drawer/drawer-base'
import type {Ref} from 'vue'
import {useBattleStore} from '~/stores/battle'
import type {BaseModel} from '@/playground/data/model'
import {BattleUtils} from "~/playground/utils/battle-utils";
import {ShapeNames, type TrapezeShape} from "~/playground/data/shapes";
import type {BodyPosition, Position} from "~/playground/data/common";
import {Trapeze} from "~/playground/data/geometry";
import type {CapturePoints} from "~/playground/data/state";

export function useBaseDrawer(
  drawerBase: DrawerBase,
  ctx: Ref<CanvasRenderingContext2D | undefined>
) {
  const battleStore = useBattleStore()

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
    const floorShape: TrapezeShape = {
      name: ShapeNames.TRAPEZE,
      height: 0.1,
      bottomRadius: baseModel.specs.radius,
      topRadius: baseModel.specs.radius
    }
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
    drawerBase.drawTrapeze(new Trapeze(centerBottom, floorShape), {fillStyle: 'white'})
    drawerBase.drawTrapeze(new Trapeze(centerBottom, flagpoleShape), {fillStyle: 'white'})
    drawerBase.drawTrapeze(new Trapeze(flagPosition, flagShape), {fillStyle: 'white'})
    drawCaptureBar(baseModel, centerBottom)
  }

  function drawCaptureBar(baseModel: BaseModel, position: Position) {
    const sumCapturePoints = Math.min(
        getSumCapturePoints(baseModel.state.capturePoints),
        baseModel.specs.capturePoints
    )
    if (sumCapturePoints > 0) {
      ctx.value!.beginPath()
      ctx.value!.lineWidth = 1
      const barHeight = 0.07
      const barWidth = 1.5 * baseModel.specs.radius
      const barTopLeft = drawerBase.transformPosition({
        x: position.x - barWidth / 2,
        y: position.y + barWidth + baseModel.specs.radius * 1.5
      })
      const hpRatio = sumCapturePoints / baseModel.specs.capturePoints
      ctx.value!.strokeRect(barTopLeft.x, barTopLeft.y,
          drawerBase.scale(barWidth), drawerBase.scale(barHeight))
      ctx.value!.fillRect(barTopLeft.x, barTopLeft.y,
          drawerBase.scale(barWidth * hpRatio), drawerBase.scale(barHeight))
      ctx.value!.closePath()
    }
  }

  function getSumCapturePoints(capturePoints: CapturePoints) {
    return Object.values(capturePoints).reduce((a, c) => a + c, 0)
  }

  return { draw }
}
