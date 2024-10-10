import type { DrawerBase } from '@/playground/composables/drawer/drawer-base'
import type { Ref } from 'vue'
import { useBattleStore } from '@/playground/stores/battle'
import type { ExplosionModel } from '@/playground/data/model'

export function useExplosionDrawer(
  drawerBase: DrawerBase,
  ctx: Ref<CanvasRenderingContext2D | undefined>
) {
  const battleStore = useBattleStore()

  function draw() {
    if (battleStore.explosions) {
      Object.values(battleStore.explosions).forEach(drawExplosion)
    }
  }

  function drawExplosion(explosionModel: ExplosionModel) {
    if (ctx.value) {
      ctx.value.fillStyle = 'rgb(256 256 256)'
      ctx.value.lineWidth = 1
      ctx.value.beginPath()
      const position = drawerBase.transformPosition(explosionModel.state.position)
      const radius = drawerBase.scale(explosionModel.state.radius)
      ctx.value.arc(position.x, position.y, radius, 0, 2 * Math.PI)
      ctx.value.fill()
      ctx.value.closePath()
    }
  }

  return { draw }
}
