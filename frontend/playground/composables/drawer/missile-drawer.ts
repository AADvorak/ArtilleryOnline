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
      ctx.value.fillStyle = 'rgb(256 256 256)'
      ctx.value.lineWidth = 1
      ctx.value.beginPath()
      const position = missileModel.state.position
      const angle = position.angle
      const length = missileModel.specs.length

      const width = drawerBase.scale(missileModel.specs.caliber)
      const center = drawerBase.transformPosition(position)
      const head = drawerBase.transformPosition(BattleUtils.shiftedPosition(position, length / 2, angle))
      const tail = drawerBase.transformPosition(BattleUtils.shiftedPosition(position, - length / 2, angle))

      ctx.value.arc(center.x, center.y, width / 2, 0, 2 * Math.PI)
      ctx.value.fill()

      ctx.value.lineWidth = width
      ctx.value.moveTo(head.x, head.y)
      ctx.value.lineTo(tail.x, tail.y)
      ctx.value.stroke()

      ctx.value.closePath()
    }
  }

  return { draw }
}
