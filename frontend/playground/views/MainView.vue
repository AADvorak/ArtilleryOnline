<script setup lang="ts">
import BattleHeader from "@/playground/components/BattleHeader.vue";
import BattleCanvas from "@/playground/components/BattleCanvas.vue";
import FinishBattleDialog from "@/playground/components/FinishBattleDialog.vue";
import {useBattleUpdater} from "~/playground/composables/battle-updater";
import {useKeyboardListener} from "~/playground/composables/keyboard-listener";
import {useCommandsSender} from "~/playground/composables/commands-sender";
import {useBattleProcessor} from "~/playground/battle/processor/battle-processor";
import {computed} from "vue";
import {useSettingsStore} from "~/stores/settings";
import {useContinuousSoundsPlayer} from "~/playground/composables/sound/continuous-sounds-player";
import {usePlayer} from "~/playground/audio/player";
import {useRoomStore} from "~/stores/room";
import {useBattleStore} from "~/stores/battle";
import {useRouter} from "#app";
import ControlButtons from "~/playground/components/controls/ControlButtons.vue";
import {AppearancesNames} from "~/dictionary/appearances-names";
import {useUserSettingsStore} from "~/stores/user-settings";
import {useColliderKeyboardListener} from "~/playground/composables/collider-keyboard-listener";
import {BattleType} from "~/playground/data/battle";
import ControlJoysticks from "~/playground/components/controls/ControlJoysticks.vue";
import {ControlsTypes} from "~/dictionary/controls-types";

const player = usePlayer()
const battleUpdater = useBattleUpdater(player)
const continuousSoundsPlayer = useContinuousSoundsPlayer(player)
const commandsSender = useCommandsSender()
const keyboardListener = useKeyboardListener(commandsSender)
const colliderKeyboardListener = useColliderKeyboardListener(commandsSender)
const battleStore = useBattleStore()
const roomStore = useRoomStore()
const userSettingsStore = useUserSettingsStore()
const router = useRouter()
const battleProcessor = useBattleProcessor()

const isMobileBrowser = ref<boolean>(false)

const isClientProcessing = computed(() => useSettingsStore().settings?.clientProcessing)
const showScreenControls = computed(() => {
  return isMobileBrowser.value || userSettingsStore
      .appearancesOrDefaultsNameValueMapping[AppearancesNames.SHOW_CONTROL_BUTTONS] === '1'
})
const screenControlsType = computed(() => userSettingsStore
    .appearancesOrDefaultsNameValueMapping[AppearancesNames.CONTROLS_TYPE])
const isButtons = computed(() => screenControlsType.value === ControlsTypes.BUTTONS)
const isJoysticks = computed(() => screenControlsType.value === ControlsTypes.JOYSTICKS)
const isCollider = computed(() => battleStore.battle?.type === BattleType.COLLIDER)

watch(() => battleStore.battle, value => {
  if (!value) {
    router.push(roomStore.room ? '/rooms/room' : '/')
  }
})

onMounted(() => {
  calculateIsMobileBrowser()
  isCollider.value ? colliderKeyboardListener.startListening() : keyboardListener.startListening()
  battleUpdater.subscribe()
  isClientProcessing.value && battleProcessor.startProcessing()
  continuousSoundsPlayer.start()
})

onBeforeUnmount(() => {
  isCollider.value ? colliderKeyboardListener.stopListening() : keyboardListener.stopListening()
  battleProcessor.stopProcessing()
  battleUpdater.unsubscribe()
  continuousSoundsPlayer.stopAll()
})

function calculateIsMobileBrowser() {
  const userAgent = navigator.userAgent || navigator.vendor
  isMobileBrowser.value = /Android|webOS|iPhone|iPad|iPod|BlackBerry|Windows Phone/i.test(userAgent)
}
</script>

<template>
  <BattleHeader :show-control-buttons="showScreenControls" />
  <BattleCanvas />
  <template v-if="showScreenControls">
    <ControlButtons v-if="isButtons" :mouse-events="!isMobileBrowser" />
    <ControlJoysticks v-if="isJoysticks" />
  </template>
  <FinishBattleDialog />
  <connection-lost-dialog />
</template>
