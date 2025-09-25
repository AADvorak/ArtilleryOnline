import type { DrawerBase } from '@/playground/composables/drawer/drawer-base'
import type { Ref } from 'vue'
import { useBattleStore } from '~/stores/battle'
import type {ParticleModel} from '@/playground/data/model'
import type {Position} from "~/playground/data/common";
import type {ParticleConfig} from "~/playground/data/config";

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
      const rawPosition = particleModel.state.position
      const velocity = particleModel.state.velocity
      const position = drawerBase.transformPosition(rawPosition)
      if (particleModel.config.text) {
        drawText(position, particleModel.config)
        return
      }
      const timeStep = 0.03
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

  function drawText(position: Position, config: ParticleConfig) {
    ctx.value!.beginPath()
    ctx.value!.fillStyle = config.color || 'rgb(256 256 256)'
    const size = 16
    ctx.value!.font = drawerBase.getFont(size)
    ctx.value!.lineWidth = 1
    ctx.value!.fillText(config.text!, position.x, position.y, size * config.text!.length)
    ctx.value!.closePath()
  }

  return { draw }
}
