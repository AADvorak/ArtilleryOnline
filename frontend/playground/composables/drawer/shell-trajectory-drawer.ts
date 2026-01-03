import type {DrawerBase} from '@/playground/composables/drawer/drawer-base'
import type {Ref} from 'vue'
import {useBattleStore} from '~/stores/battle'
import {useSettingsStore} from "~/stores/settings";
import {type Position} from "~/playground/data/common";

export function useShellTrajectoryDrawer(
    drawerBase: DrawerBase,
    ctx: Ref<CanvasRenderingContext2D | undefined>
) {
  const battleStore = useBattleStore()
  const settingsStore = useSettingsStore()

  function draw() {
    if (settingsStore.settings?.debug && battleStore.shellTrajectory) {
      drawTrajectory(battleStore.shellTrajectory)
    }
  }

  function drawTrajectory(trajectory: Position[]) {
    if (!trajectory.length) {
      return
    }
    ctx.value!.strokeStyle = 'rgb(150,150,150)'
    ctx.value!.lineWidth = 1
    ctx.value!.beginPath()
    let pos = drawerBase.transformPosition(trajectory[0]!)
    ctx.value!.moveTo(pos.x, pos.y)
    for (let i = 1; i < trajectory.length; i++) {
      pos = drawerBase.transformPosition(trajectory[i]!)
      ctx.value!.lineTo(pos.x, pos.y)
    }
    ctx.value!.stroke()
    ctx.value!.closePath()
  }

  return {draw}
}
