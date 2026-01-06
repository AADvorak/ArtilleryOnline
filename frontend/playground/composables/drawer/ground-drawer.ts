import type {DrawerBase} from '@/playground/composables/drawer/drawer-base'
import {type Ref} from 'vue'
import {useBattleStore} from '~/stores/battle'
import type {RoomModel} from '@/playground/data/model'
import {BattleUtils} from "~/playground/utils/battle-utils";

export function useGroundDrawer(
  drawerBase: DrawerBase,
  ctx: Ref<CanvasRenderingContext2D | undefined>
) {
  const battleStore = useBattleStore()

  function draw() {
    const roomModel = battleStore.battle?.model.room
    const groundLine = roomModel?.state.groundLine
    if (ctx.value && roomModel && groundLine) {
      ctx.value.fillStyle = drawerBase.getGroundFillStyle()
      ctx.value.lineWidth = 1
      ctx.value.beginPath()
      let position = getGroundPosition(0, roomModel)
      ctx.value.moveTo(position.x, position.y)
      for (let i = 1; i < groundLine.length; i++) {
        position = getGroundPosition(i, roomModel)
        ctx.value.lineTo(position.x, position.y)
      }
      position = drawerBase.transformPosition({
        x: roomModel.specs.rightTop.x,
        y: 0
      })
      ctx.value.lineTo(position.x, position.y)
      position = drawerBase.transformPosition({
        x: roomModel.specs.leftBottom.x,
        y: 0
      })
      ctx.value.lineTo(position.x, position.y)
      ctx.value.fill()
      ctx.value.closePath()
    }
  }

  function getGroundPosition(i: number, roomModel: RoomModel) {
    return drawerBase.transformPosition(BattleUtils.getGroundPosition(i, roomModel))
  }

  return { draw }
}
