<script setup lang="ts">
import {useRouter} from "#app";
import {ApiRequestSender} from "~/api/api-request-sender";
import {useBattleStore} from "~/stores/battle";
import {useQueueStore} from "~/stores/queue";
import {useSettingsStore} from "~/stores/settings";
import type {Battle} from "~/playground/data/battle";
import type {UserBattleQueueParams, UserBattleQueueResponse} from "~/data/response";
import {DateUtils} from "~/utils/DateUtils";
import {usePresetsStore} from "~/stores/presets";
import {useRequestErrorHandler} from "~/composables/request-error-handler";

const router = useRouter()
const battleStore = useBattleStore()
const queueStore = useQueueStore()
const settingsStore = useSettingsStore()
const presetsStore = usePresetsStore()
const api = new ApiRequestSender()

const selectedVehicle = ref<string>()

const vehicles = computed(() => {
  return Object.keys(presetsStore.vehicles)
})

watch(() => battleStore.battle, (value) => {
  if (value) {
    setTimeout(() => router.push('/playground'))
  }
})
watch(() => queueStore.queue, () => {
  if (checkUserInQueue()) {
    selectedVehicle.value = queueStore.queue!.params.selectedVehicle
    loadBattle()
  }
})

onMounted(() => {
  if (checkUserInQueue()) {
    selectedVehicle.value = queueStore.queue!.params.selectedVehicle
    loadBattle()
  }
})

async function randomBattle() {
  try {
    const request = {
      selectedVehicle: selectedVehicle.value!
    }
    queueStore.queue = await api.putJson<UserBattleQueueParams, UserBattleQueueResponse>('/battles/queue', request)
  } catch (e) {
    useRequestErrorHandler().handle(e)
  }
}

async function testDrive() {
  try {
    const request = {
      selectedVehicle: selectedVehicle.value!
    }
    const battle = await api.postJson<UserBattleQueueParams, Battle>('/battles/test-drive', request)
    battleStore.updateBattle(battle)
  } catch (e) {
    useRequestErrorHandler().handle(e)
  }
}

async function loadBattle() {
  if (checkUserInQueue()) {
    try {
      const battle = await api.getJson<Battle>('/battles')
      queueStore.queue = undefined
      battleStore.updateBattle(battle)
    } catch (e) {
      setTimeout(loadBattle, 1000)
    }
  }
}

function checkUserInQueue() {
  if (!queueStore.queue) {
    return false
  }
  const addDate = DateUtils.getClientDate(queueStore.queue!.addTime)
  const now = new Date()
  if (now.getTime() - addDate.getTime() > settingsStore.settings!.userBattleQueueTimeout) {
    queueStore.queue = undefined
    return false
  }
  return true
}

async function cancel() {
  await api.delete('/battles/queue')
  queueStore.queue = undefined
}

function back() {
  router.push('/menu')
}
</script>

<template>
  <NuxtLayout>
    <v-card width="100%" max-width="600px">
      <v-card-title>
        Artillery online: battle
      </v-card-title>
      <v-card-text>
        <v-form>
          <v-select
              v-model="selectedVehicle"
              :items="vehicles"
              :disabled="!!queueStore.queue"
              density="compact"
              label="Select vehicle"
          />
        </v-form>
        <v-btn class="mb-4" width="100%" color="error" :loading="!!queueStore.queue"
               :disabled="!!battleStore.battle || !selectedVehicle"
               @click="randomBattle">Random battle</v-btn>
        <v-btn v-show="!!queueStore.queue" class="mb-4" width="100%" @click="cancel">Cancel</v-btn>
        <v-btn class="mb-4" width="100%" color="secondary"
               :disabled="!!queueStore.queue || !!battleStore.battle || !selectedVehicle"
               @click="testDrive">Test drive</v-btn>
        <v-btn class="mb-4" width="100%" :disabled="!!queueStore.queue" @click="back">Back</v-btn>
      </v-card-text>
    </v-card>
  </NuxtLayout>
</template>
