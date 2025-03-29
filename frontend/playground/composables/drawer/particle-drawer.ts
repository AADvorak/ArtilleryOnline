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
      const timeStep = 0.03
      const rawPosition = particleModel.state.position
      const velocity = particleModel.state.velocity
      const position = drawerBase.transformPosition(rawPosition)
      const nextPosition = drawerBase.transformPosition({
        x: rawPosition.x + velocity.x * timeStep,
        y: rawPosition.y + velocity.y * timeStep,
      })
      ctx.value.strokeStyle = particleModel.config.color || 'rgb(256 256 256)'
      ctx.value.lineWidth = 1
      ctx.value.beginPath()
      ctx.value.moveTo(position.x, position.y)
      ctx.value.lineTo(nextPosition.x, nextPosition.y)
      ctx.value.stroke()
      ctx.value.closePath()
    }
  }

  return { draw }
}
