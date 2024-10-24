<script setup lang="ts">
import {computed} from "vue";
import {useBattleStore} from "~/stores/battle";
import ReloadingProgress from "@/playground/components/ReloadingProgress.vue";
import HitPointsBar from "@/playground/components/HitPointsBar.vue";
import BattleTimer from "@/playground/components/BattleTimer.vue";
import type {StompClient} from "@/playground/composables/stomp-client";
import BattleDebugButtons from "@/playground/components/BattleDebugButtons.vue";
import {useSettingsStore} from "~/stores/settings";
import JetBar from "~/playground/components/JetBar.vue";
import LeaveBattleDialog from "~/playground/components/LeaveBattleDialog.vue";

const props = defineProps<{
  stompClient: StompClient
}>()

const battleStore = useBattleStore()
const settingsStore = useSettingsStore()

const leaveBattleDialog = ref<InstanceType<typeof LeaveBattleDialog> | null>(null)

const userKeys = computed(() => {
  return Object.keys(battleStore.vehicles || {})
})

const isDebugMode = computed(() => settingsStore.settings?.debug)

function showLeaveBattleDialog() {
  leaveBattleDialog.value?.show()
}
</script>

<template>
  <v-app-bar>
    <div v-if="isDebugMode" class="ml-5">
      <BattleDebugButtons :stomp-client="props.stompClient"/>
    </div>
    <div class="ml-5 battle-timer-wrapper">
      <BattleTimer />
    </div>
    <div class="ml-5 hit-points-bar-wrapper">
      <HitPointsBar v-for="userKey in userKeys" :user-key="userKey" />
    </div>
    <div class="ml-5 jet-bar-wrapper">
      <JetBar />
    </div>
    <div v-if="battleStore.isActive" class="ml-5">
      <ReloadingProgress :stomp-client="props.stompClient"/>
    </div>
    <v-spacer/>
    <v-btn @click="showLeaveBattleDialog">Leave battle</v-btn>
    <LeaveBattleDialog ref="leaveBattleDialog" :stomp-client="props.stompClient"/>
  </v-app-bar>
</template>

<style scoped>
.battle-timer-wrapper {
  min-width: 50px;
}

.hit-points-bar-wrapper {
  min-width: 200px;
}

.jet-bar-wrapper {
  min-width: 200px;
}
</style>
