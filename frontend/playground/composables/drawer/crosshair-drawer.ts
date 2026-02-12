import type {DrawerBase} from '@/playground/composables/drawer/drawer-base'
import {useBattleStore} from '~/stores/battle'
import {BattleUtils} from "~/playground/utils/battle-utils";
import {Circle, Segment} from "~/playground/data/geometry";
import {type Position, type TargetData} from "~/playground/data/common";
import {VectorUtils} from "~/playground/utils/vector-utils";
import {useUserStore} from "~/stores/user";
import {computed} from "vue";

const CROSSHAIR_SIZE = 0.25
const CROSSHAIR_INNER_SIZE = 0.08
const CROSSHAIR_COLORS = {
  DEFAULT: 'rgb(255,255,255)',
  DECONCENTRATED: 'rgb(150,150,150)',
  HARD_PENETRATION: 'rgb(255,3,3)',
  MODERATE_PENETRATION: 'rgb(255,98,1)',
  MODERATE_EASY_PENETRATION: 'rgb(255,204,1)',
  EASY_PENETRATION: 'rgb(93,252,2)',
}

export function useCrosshairDrawer(
    drawerBase: DrawerBase
) {
  const battleStore = useBattleStore()
  const userStore = useUserStore()

  const userVehicle = computed(() => {
    return battleStore.battle?.model.vehicles[userStore.user!.nickname]
  })

  const reloadingProgress = computed(() => {
    if (!userVehicle.value) {
      return 0
    }
    const loadTime = userVehicle.value.config.gun.loadTime
    const loadRemainTime = userVehicle.value.state.gunState.loadRemainTime
    return (2 * Math.PI * (loadTime - loadRemainTime)) / loadTime
  })

  function draw() {
    const targetData = battleStore.targetData
    if (targetData) {
      drawCrosshair(
          targetData.contact.position,
          0.0,
          getCrosshairColor(targetData)
      )
    }
  }

  function drawCrosshair(position: Position, angle: number, color: string) {
    drawerBase.setDrawParams({lineWidth: 2, strokeStyle: color})
    drawerBase.drawSegment(new Segment(
        BattleUtils.shiftedPosition(position, CROSSHAIR_INNER_SIZE, angle - Math.PI / 2),
        BattleUtils.shiftedPosition(position, CROSSHAIR_SIZE, angle - Math.PI / 2),
    ))
    drawerBase.drawSegment(new Segment(
        BattleUtils.shiftedPosition(position, -CROSSHAIR_INNER_SIZE, angle - Math.PI / 2),
        BattleUtils.shiftedPosition(position, -CROSSHAIR_SIZE, angle - Math.PI / 2),
    ))
    drawerBase.drawSegment(new Segment(
        BattleUtils.shiftedPosition(position, CROSSHAIR_INNER_SIZE, angle),
        BattleUtils.shiftedPosition(position, CROSSHAIR_SIZE, angle),
    ))
    drawerBase.drawSegment(new Segment(
        BattleUtils.shiftedPosition(position, -CROSSHAIR_INNER_SIZE, angle),
        BattleUtils.shiftedPosition(position, -CROSSHAIR_SIZE, angle),
    ))
    drawerBase.drawArc(position, CROSSHAIR_INNER_SIZE, 0, reloadingProgress.value, {stroke: true})
  }

  function getCrosshairColor(targetData: TargetData) {
    if (targetData.deconcentrated) {
      return CROSSHAIR_COLORS.DECONCENTRATED
    }
    if (!targetData.armor || !targetData.penetration) {
      return CROSSHAIR_COLORS.DEFAULT
    }
    const angleCoefficient = VectorUtils.dotProduct(targetData.hitNormal, targetData.contact.normal)
    const resultPenetration = angleCoefficient * angleCoefficient * targetData.penetration
    const ratio = resultPenetration / targetData.armor
    if (ratio > 1.1) {
      return CROSSHAIR_COLORS.EASY_PENETRATION
    }
    if (ratio > 1.0) {
      return CROSSHAIR_COLORS.MODERATE_EASY_PENETRATION
    }
    if (ratio > 0.9) {
      return CROSSHAIR_COLORS.MODERATE_PENETRATION
    }
    return CROSSHAIR_COLORS.HARD_PENETRATION
  }

  return {draw}
}
