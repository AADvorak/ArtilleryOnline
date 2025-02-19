import type { DrawerBase } from '@/playground/composables/drawer/drawer-base'
import type { Ref } from 'vue'
import { useBattleStore } from '~/stores/battle'
import type { ShellModel } from '@/playground/data/model'
import {VectorUtils} from "~/playground/utils/vector-utils";
import {BattleUtils} from "~/playground/utils/battle-utils";

export function useShellDrawer(
  drawerBase: DrawerBase,
  ctx: Ref<CanvasRenderingContext2D | undefined>
) {
  const battleStore = useBattleStore()

  function draw() {
    if (battleStore.shells) {
      Object.values(battleStore.shells).forEach(drawShell)
    }
  }

  function drawShell(shellModel: ShellModel) {
    if (ctx.value) {
      const caliber = shellModel.specs.caliber
      const rawHead = shellModel.state.position
      const angle = VectorUtils.getAngle(shellModel.state.velocity)
      const rawTail = BattleUtils.shiftedPosition(rawHead, - 3 * caliber, angle)
      const head = drawerBase.transformPosition(rawHead)
      const tailRight = drawerBase.transformPosition(BattleUtils.shiftedPosition(rawTail, caliber, angle + Math.PI / 2))
      const tailLeft = drawerBase.transformPosition(BattleUtils.shiftedPosition(rawTail, caliber, angle - Math.PI / 2))

      ctx.value.fillStyle = 'rgb(256 256 256)'
      ctx.value.lineWidth = 1
      ctx.value.beginPath()
      ctx.value.moveTo(head.x, head.y)
      ctx.value.lineTo(tailRight.x, tailRight.y)
      ctx.value.lineTo(tailLeft.x, tailLeft.y)
      ctx.value.fill()
      ctx.value.closePath()
    }
  }

  return { draw }
}
