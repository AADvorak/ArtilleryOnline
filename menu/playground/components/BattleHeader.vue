<script setup lang="ts">
import {computed, onMounted, ref} from "vue";
import {useBattleLoader} from "@/playground/composables/battle-loader";
import {ApiRequestSender} from "@/playground/api/api-request-sender";
import {useBattleStore} from "@/playground/stores/battle";
import ReloadingProgress from "@/playground/components/ReloadingProgress.vue";
import HitPointsBar from "@/playground/components/HitPointsBar.vue";
import BattleTimer from "@/playground/components/BattleTimer.vue";
import type {StompClient} from "@/playground/composables/stomp-client";
import BattleDebugButtons from "@/playground/components/BattleDebugButtons.vue";
import {useSettingsStore} from "@/playground/stores/settings";
import {useUserKeyStore} from "@/playground/stores/user-key";

const props = defineProps<{
  stompClient: StompClient
}>()

const userKey = ref('Player')
const userStore = useUserKeyStore()
const battleStore = useBattleStore()
const settingsStore = useSettingsStore()
const apiRequestSender = new ApiRequestSender()

const userKeys = computed(() => {
  return Object.keys(battleStore.vehicles)
})

const isDebugMode = computed(() => settingsStore.settings?.debug)

onMounted(() => {
  const userKeyFromLocalStorage = localStorage.getItem('userKey')
  if (userKeyFromLocalStorage) {
    userKey.value = userKeyFromLocalStorage
  }
})

async function toBattle() {
  localStorage.setItem('userKey', userKey.value)
  userStore.userKey = userKey.value
  await apiRequestSender.putJson<undefined, void>('/battles/queue', userStore.userKey as string, undefined)
  useBattleLoader().startBattleLoading()
}
</script>

<template>
  <v-app-bar>
    <template v-if="!battleStore.battle">
      <v-text-field
          v-model="userKey"
          label="Nickname"
          style="max-width: 150px"
          type="text"
          :disabled="!!userStore.userKey"
      />
      <v-btn
          color="error"
          :disabled="!!userStore.userKey || !userKey"
          @click="toBattle"
      >
        Battle
      </v-btn>
    </template>
    <template v-else>
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
