import type { DrawerBase } from '@/playground/composables/drawer/drawer-base'
import {computed, type Ref} from 'vue'
import { useBattleStore } from '~/stores/battle'
import type { RoomModel } from '@/playground/data/model'
import {useUserSettingsStore} from "~/stores/user-settings";
import {AppearancesNames} from "~/dictionary/appearances-names";

export function useGroundDrawer(
  drawerBase: DrawerBase,
  ctx: Ref<CanvasRenderingContext2D | undefined>
) {
  const battleStore = useBattleStore()
  const userSettingsStore = useUserSettingsStore()

  const appearances = computed(() => userSettingsStore.appearancesOrDefaultsNameValueMapping)

  const img = new Image()
  img.src = `/images/ground-texture-${battleStore.battle?.model.room.config.groundTexture}.jpg`

  function draw() {
    const roomModel = battleStore.battle?.model.room
    if (ctx.value && roomModel) {
      ctx.value.fillStyle = getFillStyle()
      ctx.value.lineWidth = 1
      ctx.value.beginPath()
      let position = getGroundPosition(0, roomModel)
      ctx.value.moveTo(position.x, position.y)
      for (let i = 1; i < roomModel.state.groundLine.length; i++) {
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
    return drawerBase.transformPosition({
      x: roomModel.specs.step * i,
      y: roomModel.state.groundLine[i]
    })
  }

  function getFillStyle() {
    if (appearances.value[AppearancesNames.GROUND_TEXTURE_BACKGROUND] === '1') {
      return ctx.value?.createPattern(img, 'repeat')
    } else {
      return 'rgb(80 80 80)'
    }
  }

  return { draw }
}
