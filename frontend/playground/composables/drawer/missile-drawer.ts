import type { DrawerBase } from '@/playground/composables/drawer/drawer-base'
import type { Ref } from 'vue'
import { useBattleStore } from '~/stores/battle'
import type {MissileModel} from '@/playground/data/model'
import {BattleUtils} from "~/playground/utils/battle-utils";

export function useMissileDrawer(
  drawerBase: DrawerBase,
  ctx: Ref<CanvasRenderingContext2D | undefined>
) {
  const battleStore = useBattleStore()

  function draw() {
    if (battleStore.missiles) {
      Object.values(battleStore.missiles).forEach(drawMissile)
    }
  }

  function drawMissile(missileModel: MissileModel) {
    if (ctx.value) {
      const position = missileModel.state.position
      const angle = position.angle
      const length = missileModel.specs.length
      const caliber = missileModel.specs.caliber
      const tailRaw = BattleUtils.shiftedPosition(position, - length / 2, angle)
      const headRaw = BattleUtils.shiftedPosition(position, length / 2, angle)

      const center = drawerBase.transformPosition(position)
      const headRight = drawerBase.transformPosition(BattleUtils.shiftedPosition(headRaw, caliber / 2, angle + Math.PI / 2))
      const headLeft = drawerBase.transformPosition(BattleUtils.shiftedPosition(headRaw, caliber / 2, angle - Math.PI / 2))
      const tip = drawerBase.transformPosition(BattleUtils.shiftedPosition(headRaw, caliber, angle))

      const tailBodyRight = drawerBase.transformPosition(BattleUtils.shiftedPosition(tailRaw, caliber / 2, angle + Math.PI / 2))
      const tailBodyLeft = drawerBase.transformPosition(BattleUtils.shiftedPosition(tailRaw, caliber / 2, angle - Math.PI / 2))
      const tailRight = drawerBase.transformPosition(BattleUtils.shiftedPosition(tailRaw, 2 * caliber, angle + Math.PI / 2))
      const tailLeft = drawerBase.transformPosition(BattleUtils.shiftedPosition(tailRaw, 2 * caliber, angle - Math.PI / 2))

      ctx.value.fillStyle = 'rgb(256 256 256)'
      ctx.value.strokeStyle = 'rgb(256 256 256)'
      ctx.value.lineWidth = 1
      ctx.value.beginPath()

      ctx.value.moveTo(tip.x, tip.y)
      ctx.value.lineTo(headRight.x, headRight.y)
      ctx.value.lineTo(headLeft.x, headLeft.y)
      ctx.value.fill()

      ctx.value.moveTo(headRight.x, headRight.y)
      ctx.value.lineTo(headLeft.x, headLeft.y)
      ctx.value.lineTo(tailBodyLeft.x, tailBodyLeft.y)
      ctx.value.lineTo(tailBodyRight.x, tailBodyRight.y)
      ctx.value.fill()

      ctx.value.moveTo(center.x, center.y)
      ctx.value.lineTo(tailRight.x, tailRight.y)
      ctx.value.lineTo(tailLeft.x, tailLeft.y)
      ctx.value.fill()

      ctx.value.closePath()
    }
  }

  return { draw }
}
