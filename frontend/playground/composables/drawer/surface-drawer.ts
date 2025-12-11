import type { DrawerBase } from '@/playground/composables/drawer/drawer-base'
import type { Ref } from 'vue'
import { useBattleStore } from '~/stores/battle'
import type {SurfaceState} from "~/playground/data/state";
import {Segment, Trapeze} from "~/playground/data/geometry";
import {VectorUtils} from "~/playground/utils/vector-utils";
import {BattleUtils} from "~/playground/utils/battle-utils";
import type {BodyPosition} from "~/playground/data/common";
import {ShapeNames} from "~/playground/data/shapes";

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
      const segment = new Segment(surface.begin, surface.end)
      const geometryPosition: BodyPosition = {
        ...VectorUtils.shifted(segment.center(), VectorUtils.multiply(segment.normal(), -surface.width / 2)),
        angle: VectorUtils.angleFromTo(surface.begin, surface.end)
      }
      const length = BattleUtils.distance(segment.begin, segment.end)
      // yes, the width of the surface is the height of rectangle
      drawerBase.drawTrapeze(ctx.value, new Trapeze(geometryPosition, {
        name: ShapeNames.TRAPEZE,
        bottomRadius: length / 2,
        topRadius: length / 2,
        height: surface.width
      }), 'rgb(256 256 256)')
    }
  }

  return { draw }
}
