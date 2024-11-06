<script setup lang="ts">
import BattleHeader from "@/playground/components/BattleHeader.vue";
import BattleCanvas from "@/playground/components/BattleCanvas.vue";
import FinishBattleDialog from "@/playground/components/FinishBattleDialog.vue";
import HelpDialog from "~/playground/components/HelpDialog.vue";
import {useBattleUpdater} from "~/playground/composables/battle-updater";
import {useKeyboardListener} from "~/playground/composables/keyboard-listener";
import {useCommandsSender} from "~/playground/composables/commands-sender";
import {useBattleProcessor} from "~/playground/battle/processor/battle-processor";
import {computed} from "vue";
import {useSettingsStore} from "~/stores/settings";

const battleUpdater = useBattleUpdater()
const keyboardListener = useKeyboardListener(useCommandsSender())

const isClientProcessing = computed(() => useSettingsStore().settings?.clientProcessing)

onMounted(() => {
  keyboardListener.startListening()
  battleUpdater.subscribe()
  isClientProcessing.value && useBattleProcessor().startProcessing()
})

onBeforeUnmount(() => {
  keyboardListener.stopListening()
  battleUpdater.unsubscribe()
})
</script>

<template>
  <BattleHeader />
  <BattleCanvas />
  <FinishBattleDialog />
  <HelpDialog />
  <connection-lost-dialog />
</template>
