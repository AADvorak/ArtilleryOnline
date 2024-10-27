<script setup lang="ts">
import {useRouter} from "#app";
import {ApiRequestSender} from "~/api/api-request-sender";
import {usePresetsStore} from "~/stores/presets";
import {useRoomStore} from "~/stores/room";
import RoomMembersTable from "~/components/room-members-table.vue";
import {useStompClientStore} from "~/stores/stomp-client";
import type {Room} from "~/data/model";
const api = new ApiRequestSender()

const router = useRouter()

const presetsStore = usePresetsStore()
const roomStore = useRoomStore()
const stompClientStore = useStompClientStore()

const selectedVehicle = ref<string>()
const openedPanels = ref<string[]>([])
const subscription = ref()

const vehicles = computed(() => {
  return Object.keys(presetsStore.vehicles)
})

onMounted(() => {
  subscribeToRoomUpdates()
})

function subscribeToRoomUpdates() {
  subscription.value = stompClientStore.client!.subscribe('/user/topic/room/updates', function (msgOut) {
    roomStore.room = JSON.parse(msgOut.body) as Room
  })
}

function unsubscribeFromRoomUpdates() {
  subscription.value.unsubscribe()
}

async function exit() {
  try {
    unsubscribeFromRoomUpdates()
    await api.delete('/rooms/exit')
    roomStore.room = null
    await router.push('/rooms')
  } catch (e) {
    console.log(e)
  }
}
</script>

<template>
  <v-card width="100%" max-width="600px">
    <v-card-title>
      Artillery online: room
    </v-card-title>
    <v-card-text>
      <v-form>
        <v-select
            v-model="selectedVehicle"
            :items="vehicles"
            density="compact"
            label="Select vehicle"
        />
      </v-form>
      <v-btn class="mb-4" width="100%" color="error">Battle</v-btn>
      <v-expansion-panels class="mb-4" v-model="openedPanels">
        <v-expansion-panel value="playersPanel">
          <v-expansion-panel-title>Players</v-expansion-panel-title>
          <v-expansion-panel-text>
            <room-members-table />
          </v-expansion-panel-text>
        </v-expansion-panel>
        <v-expansion-panel value="invitePlayersPanel">
          <v-expansion-panel-title>Invite players</v-expansion-panel-title>
          <v-expansion-panel-text>
            <online-users-table />
          </v-expansion-panel-text>
        </v-expansion-panel>
      </v-expansion-panels>
      <v-btn class="mb-4" width="100%" @click="exit">Exit</v-btn>
    </v-card-text>
  </v-card>
</template>
