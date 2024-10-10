import {useBattleStore} from "@/playground/stores/battle";
import {ApiRequestSender} from "@/playground/api/api-request-sender";
import type {Battle} from "@/playground/data/battle";
import {useUserKeyStore} from "@/playground/stores/user-key";

export function useBattleLoader() {

  const apiRequestSender = new ApiRequestSender()
  const userStore = useUserKeyStore()
  const battleStore = useBattleStore()

  async function loadBattle() {
    const battle = await apiRequestSender.getJson<Battle>('/battles', userStore.userKey as string)
    if (battle) {
      battleStore.updateBattle(battle)
    } else {
      setTimeout(loadBattle, 500)
    }
  }

  function startBattleLoading() {
    loadBattle().then()
  }

  return { startBattleLoading }
}
