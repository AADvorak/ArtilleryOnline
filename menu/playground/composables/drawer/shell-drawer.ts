import type { DrawerBase } from '@/playground/composables/drawer/drawer-base'
import type { Ref } from 'vue'
import { useBattleStore } from '~/stores/battle'
import type { ShellModel } from '@/playground/data/model'

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
      ctx.value.fillStyle = 'rgb(256 256 256)'
      ctx.value.lineWidth = 1
      ctx.value.beginPath()
      const position = drawerBase.transformPosition(shellModel.state.position)
      ctx.value.arc(position.x, position.y, 2, 0, 2 * Math.PI)
      ctx.value.fill()
      ctx.value.closePath()
    }
  }

  return { draw }
}
