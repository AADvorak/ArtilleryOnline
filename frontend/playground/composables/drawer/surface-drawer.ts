import type { DrawerBase } from '@/playground/composables/drawer/drawer-base'
import type { Ref } from 'vue'
import { useBattleStore } from '~/stores/battle'
import type {SurfaceState} from "~/playground/data/state";

export function useSurfaceDrawer(
  drawerBase: DrawerBase,
  ctx: Ref<CanvasRenderingContext2D | undefined>
) {
  const battleStore = useBattleStore()

  function draw() {
    const surfaces = battleStore.battle?.model.room.state.surfaces
    if (surfaces) {
      surfaces.forEach(drawSurface)
    }
  }

  function drawSurface(surface: SurfaceState) {
    if (ctx.value) {
      const begin = drawerBase.transformPosition(surface.begin)
      const end = drawerBase.transformPosition(surface.end)

      ctx.value.strokeStyle = 'rgb(256 256 256)'
      ctx.value.lineWidth = 3
      ctx.value.beginPath()

      ctx.value.moveTo(begin.x, begin.y)
      ctx.value.lineTo(end.x, end.y)
      ctx.value.stroke()

      ctx.value.closePath()
    }
  }

  return { draw }
}
