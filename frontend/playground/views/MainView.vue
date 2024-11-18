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

const player = usePlayer()
const battleUpdater = useBattleUpdater(player)
const continuousSoundsPlayer = useContinuousSoundsPlayer(player)
const keyboardListener = useKeyboardListener(useCommandsSender())

const isClientProcessing = computed(() => useSettingsStore().settings?.clientProcessing)

onMounted(() => {
  keyboardListener.startListening()
  battleUpdater.subscribe()
  isClientProcessing.value && useBattleProcessor().startProcessing()
  continuousSoundsPlayer.start()
})

onBeforeUnmount(() => {
  keyboardListener.stopListening()
  battleUpdater.unsubscribe()
  continuousSoundsPlayer.stopAll()
})
</script>

<template>
  <BattleHeader />
  <BattleCanvas />
  <FinishBattleDialog />
  <connection-lost-dialog />
</template>
