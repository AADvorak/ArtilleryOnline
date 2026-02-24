import type {DrawerBase, DrawParams} from '@/playground/composables/drawer/drawer-base'
import {useBattleStore} from '~/stores/battle'
import type {BodyParticleModel, ParticleModel} from '@/playground/data/model'
import type {Position} from "~/playground/data/common";
import type {ParticleConfig} from "~/playground/data/config";
import {Circle, HalfCircle, RegularPolygon, Segment, Trapeze} from "~/playground/data/geometry";
import {
  type CircleShape,
  type HalfCircleShape,
  type RegularPolygonShape,
  ShapeNames,
  type TrapezeShape
} from "~/playground/data/shapes";

export function useParticleDrawer(
  drawerBase: DrawerBase
) {
  const battleStore = useBattleStore()

  function draw() {
    if (battleStore.particles) {
      Object.values(battleStore.particles).forEach(drawParticle)
    }
    if (battleStore.bodyParticles) {
      Object.values(battleStore.bodyParticles).forEach(drawBodyParticle)
    }
  }

  function drawParticle(particleModel: ParticleModel) {
    const position = particleModel.state.position
    const velocity = particleModel.state.velocity
    if (particleModel.config.text) {
      drawText(position, particleModel.config)
      return
    }
    if (particleModel.config.icon) {
      drawerBase.drawMDIIcon(particleModel.config.icon, position, particleModel.config.color || 'rgb(256 256 256)')
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

  function drawText(position: Position, config: ParticleConfig) {
    drawerBase.drawText({
      position,
      text: config.text!,
      fontSize: 14
    }, {fillStyle: config.color || 'rgb(256 256 256)'})
  }

  function drawBodyParticle(particleModel: BodyParticleModel) {
    const shape = particleModel.config.shape
    const params: DrawParams = {}
    if (particleModel.config.groundTexture) {
      params.groundTexture = particleModel.config.groundTexture
    }
    if (particleModel.config.color) {
      params.fillStyle = particleModel.config.color
    }
    if (shape.name === ShapeNames.REGULAR_POLYGON) {
      drawerBase.drawRegularPolygon(new RegularPolygon(particleModel.state.position,
          shape as RegularPolygonShape), params)
    }
    if (shape.name === ShapeNames.CIRCLE) {
      drawerBase.drawCircle(new Circle(particleModel.state.position,
          (shape as CircleShape).radius), params)
    }
    if (shape.name === ShapeNames.HALF_CIRCLE) {
      drawerBase.drawHalfCircle(new HalfCircle(particleModel.state.position,
          (shape as HalfCircleShape).radius, particleModel.state.position.angle), params)
    }
    if (shape.name === ShapeNames.TRAPEZE) {
      drawerBase.drawTrapeze(new Trapeze(particleModel.state.position, shape as TrapezeShape), params)
    }
  }

  return { draw }
}
