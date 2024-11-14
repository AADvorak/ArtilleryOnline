import type {Position} from "~/playground/data/common";
import {useBattleStore} from "~/stores/battle";
import {useUserStore} from "~/stores/user";
import {BattleUtils} from "~/playground/utils/battle-utils";

export function useSoundsPlayerBase() {
  const battleStore = useBattleStore()
  const userStore = useUserStore()

  const userVehiclePosition = computed((): Position => {
    const userVehicle = battleStore.vehicles && battleStore.vehicles[userStore.user!.nickname]
    return userVehicle ? userVehicle.state.position : {x: 0, y: 0}
  })

  const roomParams = computed(() => {
    const xMin = battleStore.battle?.model.room.specs.leftBottom.x || 0
    const xMax = battleStore.battle?.model.room.specs.rightTop.x || 0
    const width = xMax - xMin
    return {xMax, xMin, width}
  })

  function calculatePan(x: number) {
    return 2 * (x - roomParams.value.xMax) / roomParams.value.width + 1
  }

  function calculateGain(position: Position) {
    const distanceToUserVehicle = BattleUtils.distance(position, userVehiclePosition.value)
    if (distanceToUserVehicle >= roomParams.value.width) {
      return 0
    }
    return 1 - Math.sqrt(distanceToUserVehicle / roomParams.value.width)
  }

  return {calculatePan, calculateGain}
}
