<script setup lang="ts">
import {useRouter} from "#app";
import {ApiRequestSender} from "~/api/api-request-sender";
import {useBattleStore} from "~/stores/battle";
import {useQueueStore} from "~/stores/queue";
import {useSettingsStore} from "~/stores/settings";
import type {UserBattleQueueParams, UserBattleQueueResponse} from "~/data/response";
import {DateUtils} from "~/utils/DateUtils";
import {useRequestErrorHandler} from "~/composables/request-error-handler";
import VehicleSelector from "~/components/vehicle-selector.vue";
import {useI18n} from "vue-i18n";
import {deserializeBattle} from "~/playground/data/battle-deserialize";
import {DeserializerInput} from "~/deserialization/deserializer-input";
import {useRoomStore} from "~/stores/room";

const {t} = useI18n()
const router = useRouter()
const battleStore = useBattleStore()
const queueStore = useQueueStore()
const settingsStore = useSettingsStore()
const roomStore = useRoomStore()
const api = new ApiRequestSender()

const vehicleSelector = ref<InstanceType<typeof VehicleSelector> | undefined>()

const selectedVehicle = ref<string>()

watch(() => battleStore.battle, (value) => {
  if (value) {
    setTimeout(() => router.push('/playground'))
  }
})
watch(() => queueStore.queue, () => {
  if (checkUserInQueue()) {
    vehicleSelector.value?.setSelectedVehicle(queueStore.queue!.params.selectedVehicle)
    loadBattle()
  }
})

onMounted(() => {
  if (checkUserInQueue()) {
    vehicleSelector.value?.setSelectedVehicle(queueStore.queue!.params.selectedVehicle)
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
  await singleBattle('/test-drive')
}

async function droneHunt() {
  await singleBattle('/drone-hunt')
}

async function singleBattle(path) {
  try {
    const request = {
      selectedVehicle: selectedVehicle.value!
    }
    const battleBinary = await api.postJsonForBinary<UserBattleQueueParams>('/battles' + path, request)
    const battle = deserializeBattle(new DeserializerInput(battleBinary))
    battleStore.updateBattle(battle)
  } catch (e) {
    useRequestErrorHandler().handle(e)
  }
}

async function loadBattle() {
  if (checkUserInQueue()) {
    try {
      const battleBinary = await new ApiRequestSender().getBytes('/battles')
      const battle = deserializeBattle(new DeserializerInput(battleBinary))
      queueStore.queue = undefined
      battleStore.updateBattle(battle)
    } catch (e) {
      e.status === 404 && setTimeout(loadBattle, 1000)
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
        Artillery online: {{ t('battle.title') }}
      </v-card-title>
      <v-card-text>
        <div v-if="!!roomStore.room" class="mb-4" style="color: crimson">{{ t('battle.leaveRoomToPlay') }}</div>
        <v-form>
          <vehicle-selector
              ref="vehicleSelector"
              :disabled="!!queueStore.queue"
              @select="v => selectedVehicle = v"
          />
        </v-form>
        <v-btn class="mb-4" width="100%" color="error" :loading="!!queueStore.queue"
               :disabled="!!battleStore.battle || !selectedVehicle || !!roomStore.room"
               @click="randomBattle">{{ t('battle.randomBattle') }}</v-btn>
        <v-btn v-show="!!queueStore.queue" class="mb-4" width="100%" @click="cancel">{{ t('common.cancel') }}</v-btn>
        <v-btn class="mb-4" width="100%" color="secondary"
               :disabled="!!queueStore.queue || !!battleStore.battle || !selectedVehicle || !!roomStore.room"
               @click="testDrive">{{ t('battle.testDrive') }}</v-btn>
        <v-btn class="mb-4" width="100%" color="secondary"
               :disabled="!!queueStore.queue || !!battleStore.battle || !selectedVehicle || !!roomStore.room"
               @click="droneHunt">{{ t('battle.droneHunt') }}</v-btn>
        <v-btn class="mb-4" width="100%" :disabled="!!queueStore.queue" @click="back">{{ t('common.back') }}</v-btn>
      </v-card-text>
    </v-card>
  </NuxtLayout>
</template>
