import type {DrawerBase} from '@/playground/composables/drawer/drawer-base'
import type {Ref} from 'vue'
import {useBattleStore} from '~/stores/battle'
import type {SurfaceState} from "~/playground/data/state";
import {Trapeze} from "~/playground/data/geometry";

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
      drawerBase.drawTrapeze(ctx.value, Trapeze.ofSurface(surface), 'rgb(256 256 256)')
    }
  }

  return { draw }
}
