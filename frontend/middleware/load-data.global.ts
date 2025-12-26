import {useUserStore} from "~/stores/user";
import {useSettingsStore} from "~/stores/settings";
import {useBattleStore} from "~/stores/battle";
import {useCsrfStore} from "~/stores/csrf";
import {useQueueStore} from "~/stores/queue";
import {useUserSettingsStore} from "~/stores/user-settings";
import {useStompClientStore} from "~/stores/stomp-client";
import {usePresetsStore} from "~/stores/presets";
import {useRoomStore} from "~/stores/room";
import {useMessageStore} from "~/stores/message";
import {useConfigsStore} from "~/stores/configs";

const ROOT_PATH = '/'
const MENU_PATH = '/menu'
const BATTLE_PATH = '/battle'
const ROOM_PATH = '/rooms/room'
const ROOMS_PATH = '/rooms'
const PLAYGROUND_PATH = '/playground'
const UNAVAILABLE_PATH = '/unavailable'
const UNSIGNED_PATHS = [ROOT_PATH, '/login', '/signup', UNAVAILABLE_PATH]

export default defineNuxtRouteMiddleware(async (to) => {
  const userStore = useUserStore()
  const settingsStore = useSettingsStore()
  const battleStore = useBattleStore()
  const csrfStore = useCsrfStore()
  const queueStore = useQueueStore()
  const userSettingsStore = useUserSettingsStore()
  const stompClientStore = useStompClientStore()
  const presetsStore = usePresetsStore()
  const roomStore = useRoomStore()
  const messageStore = useMessageStore()
  const configsStore = useConfigsStore()

  try {
    await Promise.all([
      settingsStore.loadIfNull(),
      userStore.loadUserIfNull()
    ])
  } catch (e) {
    console.log(e)
    // @ts-ignore
    if (e.status >= 500) {
      if (UNAVAILABLE_PATH !== to.path) {
        return navigateTo(UNAVAILABLE_PATH)
      } else {
        return
      }
    } else if (UNAVAILABLE_PATH === to.path) {
      return navigateTo(ROOT_PATH)
    }
  }

  if (!!userStore.user) {
    await Promise.all([
      stompClientStore.connect(),
      messageStore.loadMessagesIfNull(),
      messageStore.loadInvitationsIfNull(),
      userSettingsStore.loadSettingsIfNull(),
      presetsStore.loadVehiclesIfNull(),
      roomStore.loadRoomIfNull(),
      csrfStore.loadCsrfIfNull(),
      to.path === PLAYGROUND_PATH ? battleStore.loadBattle() : battleStore.loadBattleIfNull()
    ])
    messageStore.subscribe()
  } else {
    messageStore.clear()
    userSettingsStore.clear()
    configsStore.clear()
    roomStore.clear()
    queueStore.clear()
    battleStore.clear()
    queueStore.clear()
  }

  if (!!userStore.user && !battleStore.battle) {
    await queueStore.loadQueueIfNull()
  }

  if (!userStore.user && !UNSIGNED_PATHS.includes(to.path)) {
    return navigateTo(ROOT_PATH)
  }
  if (!!userStore.user && !!queueStore.queue && to.path !== BATTLE_PATH) {
    return navigateTo(BATTLE_PATH)
  }
  if (!!userStore.user && !!battleStore.battle && to.path === BATTLE_PATH) {
    return navigateTo(PLAYGROUND_PATH)
  }
  if (!!userStore.user && !roomStore.room && to.path === ROOM_PATH) {
    return navigateTo(ROOMS_PATH)
  }
  if (!!userStore.user && !battleStore.battle && to.path === PLAYGROUND_PATH) {
    return navigateTo(MENU_PATH)
  }
  if (!!userStore.user && UNSIGNED_PATHS.includes(to.path)) {
    return navigateTo(MENU_PATH)
  }
})
