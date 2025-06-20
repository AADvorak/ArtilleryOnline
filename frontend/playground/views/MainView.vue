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
import ControlButtons from "~/playground/components/ControlButtons.vue";
import {AppearancesNames} from "~/dictionary/appearances-names";
import {useUserSettingsStore} from "~/stores/user-settings";

const player = usePlayer()
const battleUpdater = useBattleUpdater(player)
const continuousSoundsPlayer = useContinuousSoundsPlayer(player)
const commandsSender = useCommandsSender()
const keyboardListener = useKeyboardListener(commandsSender)
const battleStore = useBattleStore()
const roomStore = useRoomStore()
const router = useRouter()
const battleProcessor = useBattleProcessor()

const isMobileBrowser = ref<boolean>(false)

const isClientProcessing = computed(() => useSettingsStore().settings?.clientProcessing)
const showControlButtons = computed(() => {
  return isMobileBrowser.value || useUserSettingsStore()
      .appearancesOrDefaultsNameValueMapping[AppearancesNames.SHOW_CONTROL_BUTTONS] === '1'
})

watch(() => battleStore.battle, value => {
  if (!value) {
    router.push(roomStore.room ? '/rooms/room' : '/')
  }
})

onMounted(() => {
  calculateIsMobileBrowser()
  keyboardListener.startListening()
  battleUpdater.subscribe()
  isClientProcessing.value && battleProcessor.startProcessing()
  continuousSoundsPlayer.start()
})

onBeforeUnmount(() => {
  keyboardListener.stopListening()
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
  <BattleHeader :show-control-buttons="showControlButtons" />
  <BattleCanvas />
  <ControlButtons v-if="showControlButtons" />
  <FinishBattleDialog />
  <connection-lost-dialog />
</template>
