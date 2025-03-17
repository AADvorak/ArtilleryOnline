import type { DrawerBase } from '@/playground/composables/drawer/drawer-base'
import type { Ref } from 'vue'
import { useBattleStore } from '~/stores/battle'
import type { ShellModel } from '@/playground/data/model'
import {VectorUtils} from "~/playground/utils/vector-utils";
import {BattleUtils} from "~/playground/utils/battle-utils";
import {ShellType} from "~/playground/data/common";

export function useShellDrawer(
  drawerBase: DrawerBase,
  ctx: Ref<CanvasRenderingContext2D | undefined>
) {
  const battleStore = useBattleStore()

  function draw() {
    if (battleStore.shells) {
      Object.values(battleStore.shells).filter(shell => !shell.state.stuck).forEach(drawShell)
    }
  }

  function drawShell(shellModel: ShellModel) {
    if (ctx.value) {
      const caliber = shellModel.specs.caliber
      const rawHead = shellModel.state.position
      const angle = VectorUtils.getAngle(shellModel.state.velocity)
      const rawTail = BattleUtils.shiftedPosition(rawHead, - 3 * caliber, angle)
      const head = drawerBase.transformPosition(rawHead)
      const tailRight = drawerBase.transformPosition(BattleUtils.shiftedPosition(rawTail,
          caliber, angle + Math.PI / 2))
      const tailLeft = drawerBase.transformPosition(BattleUtils.shiftedPosition(rawTail,
          caliber, angle - Math.PI / 2))

      ctx.value.lineWidth = 1

      if (ShellType.SGN === shellModel.specs.type) {
        ctx.value.fillStyle = 'rgb(248,105,4)'
        ctx.value.beginPath()
        ctx.value.arc(head.x, head.y, drawerBase.scale(caliber / 2), 0, Math.PI * 2)
        ctx.value.fill()
        ctx.value.closePath()
      } else if (ShellType.BMB === shellModel.specs.type) {
        const headCenter = drawerBase.transformPosition(BattleUtils.shiftedPosition(rawHead, - caliber / 2, angle))
        const tailCenter = drawerBase.transformPosition(BattleUtils.shiftedPosition(rawHead, - 3 * caliber / 2, angle))
        ctx.value.fillStyle = 'rgb(256 256 256)'
        ctx.value.beginPath()
        ctx.value.arc(headCenter.x, headCenter.y, drawerBase.scale(caliber / 2), 0, Math.PI * 2)
        ctx.value.fill()
        ctx.value.closePath()
        ctx.value.beginPath()
        ctx.value.arc(tailCenter.x, tailCenter.y, drawerBase.scale(caliber / 2), 3 * Math.PI / 2 - angle, 5 * Math.PI / 2 - angle)
        ctx.value.fill()
        ctx.value.closePath()
      } else {
        ctx.value.fillStyle = 'rgb(256 256 256)'
        ctx.value.beginPath()
        ctx.value.moveTo(head.x, head.y)
        ctx.value.lineTo(tailRight.x, tailRight.y)
        ctx.value.lineTo(tailLeft.x, tailLeft.y)
        ctx.value.fill()
        ctx.value.closePath()
      }
    }
  }

  return { draw }
}
