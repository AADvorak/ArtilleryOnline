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
const UNSIGNED_PATHS = ['/', '/login', '/signup']

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

  await settingsStore.loadIfNull()
  await userStore.loadUserIfNull()

  if (!!userStore.user) {
    await stompClientStore.connect()
    await messageStore.loadMessagesIfNull()
    await messageStore.loadInvitationsIfNull()
    await userSettingsStore.loadSettingsIfNull()
    await presetsStore.loadVehiclesIfNull()
    await roomStore.loadRoomIfNull()
    await csrfStore.loadCsrfIfNull()
    await battleStore.loadBattleIfNull()
    messageStore.subscribe()
  } else {
    messageStore.clear()
    userSettingsStore.clear()
    configsStore.clear()
    roomStore.clear()
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
  if (!!userStore.user && !!battleStore.battle && to.path !== PLAYGROUND_PATH) {
    return navigateTo(PLAYGROUND_PATH)
  }
  if (!!userStore.user && !!roomStore.room && !battleStore.battle && to.path !== ROOM_PATH) {
    return navigateTo(ROOM_PATH)
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
