import {useBattleStore} from "@/stores/battle";
import {ApiRequestSender} from "@/api/api-request-sender";
import type {Battle} from "@/data/battle";
import {useUserStore} from "@/stores/user";

export function useBattleLoader() {

  const apiRequestSender = new ApiRequestSender()
  const userStore = useUserStore()
  const battleStore = useBattleStore()

  async function loadBattle() {
    const battle = await apiRequestSender.getJson<Battle>('/battles', userStore.userKey as string)
    if (battle) {
      battleStore.battle = battle
    }
    setTimeout(loadBattle, 100)
  }

  function startBattleLoading() {
    loadBattle().then()
    // todo battle state loading
  }

  return { startBattleLoading }
}
