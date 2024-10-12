<script setup lang="ts">
import {useRouter} from "#app";
import {ApiRequestSender} from "~/api/api-request-sender";
import {useBattleStore} from "~/stores/battle";
import {useQueueStore} from "~/stores/queue";
import {useSettingsStore} from "~/stores/settings";
import type {Battle} from "~/playground/data/battle";
import type {UserBattleQueueResponse} from "~/data/response";

const router = useRouter()
const battleStore = useBattleStore()
const queueStore = useQueueStore()
const settingsStore = useSettingsStore()
const api = new ApiRequestSender()

watch(() => battleStore.battle, (value) => {
  if (value) {
    setTimeout(() => router.push('/playground'))
  }
})
watch(() => queueStore.addTime, () => {
  if (checkUserInQueue()) {
    loadBattle()
  }
})

onMounted(() => {
  if (checkUserInQueue()) {
    loadBattle()
  }
})

async function randomBattle() {
  try {
    const response = await api.putJson<undefined, UserBattleQueueResponse>('/battles/queue', undefined)
    queueStore.addTime = response.addTime
  } catch (e) {
    console.log(e)
  }
}

async function loadBattle() {
  if (checkUserInQueue()) {
    try {
      const battle = await api.getJson<Battle>('/battles')
      queueStore.addTime = null
      battleStore.updateBattle(battle)
    } catch (e) {
      setTimeout(loadBattle, 1000)
    }
  }
}

function checkUserInQueue() {
  if (!queueStore.addTime) {
    return false
  }
  let addDate = new Date(queueStore.addTime)
  let now = new Date()
  if (now.getTime() - addDate.getTime() > settingsStore.settings!.userBattleQueueTimeout) {
    queueStore.addTime = null
    return false
  }
  return true
}

async function cancel() {
  await api.delete('/battles/queue')
  queueStore.addTime = null
}

function back() {
  router.push('/menu')
}
</script>

<template>
  <v-card width="100%" max-width="600px">
    <v-card-title>
      Artillery online: battle
    </v-card-title>
    <v-card-text>
      <v-btn class="mb-4" width="100%" color="error" :loading="!!queueStore.addTime" :disabled="!!battleStore.battle"
             @click="randomBattle">Random battle</v-btn>
      <v-btn v-show="!!queueStore.addTime" class="mb-4" width="100%" @click="cancel">Cancel</v-btn>
      <v-btn class="mb-4" width="100%" :disabled="!!queueStore.addTime" @click="back">Back</v-btn>
    </v-card-text>
  </v-card>
</template>
