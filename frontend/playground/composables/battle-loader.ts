import {useBattleStore} from "~/stores/battle";
import {ApiRequestSender} from "@/api/api-request-sender";
import type {Battle} from "@/playground/data/battle";

export function useBattleLoader() {

  const apiRequestSender = new ApiRequestSender()
  const battleStore = useBattleStore()

  async function loadBattle() {
    try {
      const battle = await apiRequestSender.getJson<Battle>('/battles')
      battleStore.updateBattle(battle)
    } catch (e) {
      setTimeout(loadBattle, 500)
    }
  }

  function startBattleLoading() {
    loadBattle().then()
  }

  return { startBattleLoading }
}
