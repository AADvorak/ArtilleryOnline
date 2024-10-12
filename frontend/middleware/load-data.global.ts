import {useUserStore} from "~/stores/user";
import {useSettingsStore} from "~/stores/settings";
import {useBattleStore} from "~/stores/battle";
import {ApiRequestSender} from "~/api/api-request-sender";
import type {CsrfToken, User} from "~/data/model";
import type {Battle} from "~/playground/data/battle";
import {useCsrfStore} from "~/stores/csrf";
import {useQueueStore} from "~/stores/queue";

const ROOT_PATH = '/'
const MENU_PATH = '/menu'
const BATTLE_PATH = '/battle'
const PLAYGROUND_PATH = '/playground'
const UNSIGNED_PATHS = ['/', '/sign-in', '/sign-up']

export default defineNuxtRouteMiddleware(async (to) => {
  const api = new ApiRequestSender()
  const userStore = useUserStore()
  const settingsStore = useSettingsStore()
  const battleStore = useBattleStore()
  const csrfStore = useCsrfStore()
  const queueStore = useQueueStore()

  await settingsStore.loadIfNull()

  if (!userStore.user) {
    try {
      userStore.user = await api.getJson<User>('/users/me')
    } catch (e) {
      console.log(e)
    }
  }

  if (!!userStore.user && !csrfStore.csrf) {
    try {
      csrfStore.csrf = await api.getJson<CsrfToken>('/csrf')
    } catch (e) {
      console.log(e)
    }
  }

  if (!!userStore.user && !battleStore.battle) {
    try {
      const battle = await api.getJson<Battle>('/battles')
      battleStore.updateBattle(battle)
    } catch (e) {
      console.log(e)
    }
  }

  if (!!userStore.user && !battleStore.battle) {
    await queueStore.loadAddTimeIfNull()
  }

  if (!userStore.user && !UNSIGNED_PATHS.includes(to.path)) {
    return navigateTo(ROOT_PATH)
  }
  if (!!userStore.user && !!queueStore.addTime && to.path !== BATTLE_PATH) {
    return navigateTo(BATTLE_PATH)
  }
  if (!!userStore.user && !!battleStore.battle && to.path !== PLAYGROUND_PATH) {
    return navigateTo(PLAYGROUND_PATH)
  }
  if (!!userStore.user && !battleStore.battle && to.path === PLAYGROUND_PATH) {
    return navigateTo(MENU_PATH)
  }
  if (!!userStore.user && UNSIGNED_PATHS.includes(to.path)) {
    return navigateTo(MENU_PATH)
  }
})
