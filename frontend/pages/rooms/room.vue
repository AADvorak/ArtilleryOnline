<script setup lang="ts">
import {useRouter} from "#app";
import {ApiRequestSender} from "~/api/api-request-sender";
import {usePresetsStore} from "~/stores/presets";
import RoomMembersTable from "~/components/room-members-table.vue";
const api = new ApiRequestSender()

const router = useRouter()

const presetsStore = usePresetsStore()

const selectedVehicle = ref<string>()
const openedPanels = ref<string[]>([])

const vehicles = computed(() => {
  return Object.keys(presetsStore.vehicles)
})

function exit() {
  router.push('/rooms')
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
