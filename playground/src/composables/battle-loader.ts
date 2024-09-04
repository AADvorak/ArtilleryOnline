import {useBattleStore} from "@/stores/battle";
import {ApiRequestSender} from "@/api/api-request-sender";
import type {Battle} from "@/data/battle";
import {useUserStore} from "@/stores/user";

export function useBattleLoader() {

  const apiRequestSender = new ApiRequestSender()
  const userStore = useUserStore()

  async function loadBattle() {
    const battle = await apiRequestSender.getJson<Battle>('/battles', userStore.userKey as string)
    console.log('battle', battle)
    if (battle) {
      useBattleStore().battle = battle
    } else {
      setTimeout(loadBattle, 100)
    }
  }

  function startBattleLoading() {
    // todo loop
    loadBattle().then()
  }

  return { startBattleLoading }
}
