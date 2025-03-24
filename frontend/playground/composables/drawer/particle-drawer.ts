import type { DrawerBase } from '@/playground/composables/drawer/drawer-base'
import type { Ref } from 'vue'
import { useBattleStore } from '~/stores/battle'
import type {ParticleModel} from '@/playground/data/model'

export function useParticleDrawer(
  drawerBase: DrawerBase,
  ctx: Ref<CanvasRenderingContext2D | undefined>
) {
  const battleStore = useBattleStore()

  function draw() {
    if (battleStore.particles) {
      Object.values(battleStore.particles).forEach(drawParticle)
    }
  }

  function drawParticle(particleModel: ParticleModel) {
    if (ctx.value) {
      const position = drawerBase.transformPosition(particleModel.state.position)
      ctx.value.fillStyle = 'rgb(256 256 256)'
      ctx.value.beginPath()
      ctx.value.arc(position.x, position.y, 1, 0, 2 * Math.PI)
      ctx.value.fill()
      ctx.value.closePath()
    }
  }

  return { draw }
}
