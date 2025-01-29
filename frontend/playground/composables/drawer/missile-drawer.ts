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
    if (battleStore) {
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

      const width = drawerBase.scale(caliber)
      const center = drawerBase.transformPosition(position)
      const head = drawerBase.transformPosition(BattleUtils.shiftedPosition(position, length / 2, angle))
      const tail = drawerBase.transformPosition(tailRaw)
      const tailRight = drawerBase.transformPosition(BattleUtils.shiftedPosition(tailRaw, 2 * caliber, angle + Math.PI / 2))
      const tailLeft = drawerBase.transformPosition(BattleUtils.shiftedPosition(tailRaw, 2 * caliber, angle - Math.PI / 2))

      ctx.value.fillStyle = 'rgb(256 256 256)'
      ctx.value.strokeStyle = 'rgb(256 256 256)'
      ctx.value.lineWidth = 1
      ctx.value.beginPath()

      ctx.value.arc(head.x, head.y, width / 3, 0, 2 * Math.PI)
      ctx.value.fill()

      ctx.value.lineWidth = width
      ctx.value.moveTo(head.x, head.y)
      ctx.value.lineTo(tail.x, tail.y)
      ctx.value.stroke()

      ctx.value.lineWidth = 1
      ctx.value.moveTo(center.x, center.y)
      ctx.value.lineTo(tailRight.x, tailRight.y)
      ctx.value.lineTo(tailLeft.x, tailLeft.y)
      ctx.value.fill()

      ctx.value.closePath()
    }
  }

  return { draw }
}
