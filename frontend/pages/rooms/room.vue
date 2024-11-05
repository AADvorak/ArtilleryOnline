<script setup lang="ts">
import {useRouter} from "#app";
import {ApiRequestSender} from "~/api/api-request-sender";
import {usePresetsStore} from "~/stores/presets";
import {useRoomStore} from "~/stores/room";
import RoomMembersTable from "~/components/room-members-table.vue";
import {useStompClientStore} from "~/stores/stomp-client";
import type {Room} from "~/data/model";
import {useUserStore} from "~/stores/user";
import {mdiAccountMultiple, mdiAccountPlus} from '@mdi/js'

const api = new ApiRequestSender()

const router = useRouter()

const presetsStore = usePresetsStore()
const roomStore = useRoomStore()
const stompClientStore = useStompClientStore()
const userStore = useUserStore()

const selectedVehicle = ref<string>()
const openedPanels = ref<string[]>(['playersPanel'])
const subscription = ref()

const vehicles = computed(() => {
  return Object.keys(presetsStore.vehicles)
})

const readyToBattle = computed(() => {
  const members = roomStore.room?.members || []
  if (members.length <= 1) {
    return false
  }
  for (const member of members) {
    if (!member.selectedVehicle) {
      return false
    }
  }
  return true
})

watch(selectedVehicle, async (value) => {
  try {
    await api.putJson('/rooms/select-vehicle', {
      selectedVehicle: value
    })
  } catch (e) {
    console.log(e)
  }
})

onMounted(() => {
  subscribeToRoomUpdates()
  setSelectedVehicle()
})

onBeforeUnmount(() => {
  unsubscribeFromRoomUpdates()
})

function subscribeToRoomUpdates() {
  subscription.value = stompClientStore.client!.subscribe('/user/topic/room/updates', function (msgOut) {
    roomStore.room = JSON.parse(msgOut.body) as Room
    if (roomStore.room.inBattle) {
      router.push('/playground')
    } else if (roomStore.room.deleted) {
      roomStore.room = null
      router.push('/rooms')
    }
  })
}

function unsubscribeFromRoomUpdates() {
  subscription.value && subscription.value.unsubscribe()
}

function setSelectedVehicle() {
  const memberVehicle = (roomStore.room?.members || [])
      .filter(member => member.nickname === userStore.user!.nickname)
      .map(member => member.selectedVehicle)[0]
  if (memberVehicle && selectedVehicle.value !== memberVehicle) {
    selectedVehicle.value = memberVehicle
  }
}

async function startBattle() {
  try {
    await api.postJson('/rooms/start-battle', {})
    await router.push('/playground')
  } catch (e) {
    console.log(e)
  }
}

async function exit() {
  try {
    await api.delete('/rooms/exit')
    roomStore.room = null
    await router.push('/rooms')
  } catch (e) {
    console.log(e)
  }
}
</script>

<template>
  <NuxtLayout>
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
        <v-btn
            v-if="roomStore.userIsRoomOwner"
            class="mb-4"
            width="100%"
            color="error"
            :disabled="!readyToBattle"
            @click="startBattle"
        >
          Battle
        </v-btn>
        <v-expansion-panels class="mb-4" v-model="openedPanels" multiple>
          <v-expansion-panel value="playersPanel">
            <v-expansion-panel-title>
              <v-icon class="mr-2" :icon="mdiAccountMultiple"/>
              Players
            </v-expansion-panel-title>
            <v-expansion-panel-text>
              <room-members-table class="mb-4"/>
            </v-expansion-panel-text>
          </v-expansion-panel>
          <v-expansion-panel v-if="roomStore.userIsRoomOwner" value="invitePlayersPanel">
            <v-expansion-panel-title>
              <v-icon class="mr-2" :icon="mdiAccountPlus"/>
              Invite players
            </v-expansion-panel-title>
            <v-expansion-panel-text>
              <online-users-table />
            </v-expansion-panel-text>
          </v-expansion-panel>
        </v-expansion-panels>
        <v-btn class="mb-4" width="100%" @click="exit">Exit</v-btn>
      </v-card-text>
    </v-card>
  </NuxtLayout>
</template>
