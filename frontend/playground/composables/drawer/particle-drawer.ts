import type { DrawerBase } from '@/playground/composables/drawer/drawer-base'
import type { Ref } from 'vue'
import { useBattleStore } from '~/stores/battle'
import type {ParticleModel} from '@/playground/data/model'
import type {Position} from "~/playground/data/common";
import type {ParticleConfig} from "~/playground/data/config";
import {Circle, Segment} from "~/playground/data/geometry";

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
      const position = particleModel.state.position
      const velocity = particleModel.state.velocity
      if (particleModel.config.text) {
        drawText(position, particleModel.config)
        return
      }
      if (particleModel.config.size) {
        drawerBase.drawCircle(new Circle(position, particleModel.config.size / 2), {groundTexture: true})
        return
      }
      const timeStep = 0.03
      const nextPosition = {
        x: position.x + velocity.x * timeStep,
        y: position.y + velocity.y * timeStep,
      }
      drawerBase.drawSegment(new Segment(position, nextPosition),
          {strokeStyle: particleModel.config.color || 'rgb(256 256 256)', lineWidth: 1})
    }
  }

  function drawText(position: Position, config: ParticleConfig) {
    const pos = drawerBase.transformPosition(position)
    ctx.value!.fillStyle = config.color || 'rgb(256 256 256)'
    ctx.value!.beginPath()
    const size = 16
    ctx.value!.font = drawerBase.getFont(size)
    ctx.value!.lineWidth = 1
    ctx.value!.fillText(config.text!, pos.x, pos.y, size * config.text!.length)
    ctx.value!.closePath()
  }

  return { draw }
}
