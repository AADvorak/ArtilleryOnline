import type { DrawerBase } from '@/playground/composables/drawer/drawer-base'
import type { Ref } from 'vue'
import { useBattleStore } from '~/stores/battle'
import type { RoomModel } from '@/playground/data/model'

export function useGroundDrawer(
  drawerBase: DrawerBase,
  ctx: Ref<CanvasRenderingContext2D | undefined>
) {
  const battleStore = useBattleStore()

  function draw() {
    const roomModel = battleStore.battle?.model.room
    if (ctx.value && roomModel) {
      ctx.value.beginPath()
      ctx.value.fillStyle = 'rgb(100 100 100)'
      ctx.value.strokeStyle = 'rgb(100 100 100)'
      ctx.value.lineWidth = 1
      let position = getGroundPosition(0, roomModel)
      ctx.value.moveTo(position.x, position.y)
      for (let i = 1; i < roomModel.state.groundLine.length; i++) {
        position = getGroundPosition(i, roomModel)
        ctx.value.lineTo(position.x, position.y)
      }
      ctx.value.stroke()
      ctx.value.closePath()
    }
  }

  function getGroundPosition(i: number, roomModel: RoomModel) {
    return drawerBase.transformPosition({
      x: roomModel.specs.step * i,
      y: roomModel.state.groundLine[i]
    })
  }

  return { draw }
}
