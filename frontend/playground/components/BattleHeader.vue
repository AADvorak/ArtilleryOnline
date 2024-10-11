<script setup lang="ts">
import {computed} from "vue";
import {useBattleStore} from "~/stores/battle";
import ReloadingProgress from "@/playground/components/ReloadingProgress.vue";
import HitPointsBar from "@/playground/components/HitPointsBar.vue";
import BattleTimer from "@/playground/components/BattleTimer.vue";
import type {StompClient} from "@/playground/composables/stomp-client";
import BattleDebugButtons from "@/playground/components/BattleDebugButtons.vue";
import {useSettingsStore} from "~/stores/settings";

const props = defineProps<{
  stompClient: StompClient
}>()

const battleStore = useBattleStore()
const settingsStore = useSettingsStore()

const userKeys = computed(() => {
  return Object.keys(battleStore.vehicles || {})
})

const isDebugMode = computed(() => settingsStore.settings?.debug)
</script>

<template>
  <v-app-bar>
    <template v-if="battleStore.battle">
      <div v-if="isDebugMode" class="ml-5">
        <BattleDebugButtons :stomp-client="props.stompClient"/>
      </div>
      <div class="ml-5 battle-timer-wrapper">
        <BattleTimer />
      </div>
      <div class="ml-5 hit-points-bar-wrapper">
        <HitPointsBar v-for="userKey in userKeys" :user-key="userKey" />
      </div>
      <div v-if="battleStore.isActive" class="ml-5">
        <ReloadingProgress :stomp-client="props.stompClient"/>
      </div>
    </template>
  </v-app-bar>
</template>

<style scoped>
.battle-timer-wrapper {
  min-width: 50px;
}

.hit-points-bar-wrapper {
  min-width: 200px;
}
</style>
