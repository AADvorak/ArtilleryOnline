<script setup lang="ts">
import {useRouter} from "#app";
import {ApiRequestSender} from "~/api/api-request-sender";
import {useRoomStore} from "~/stores/room";
import RoomMembersTable from "~/components/room-members-table.vue";
import {useUserStore} from "~/stores/user";
import {mdiAccountMultiple, mdiAccountPlus} from '@mdi/js'
import {useRequestErrorHandler} from "~/composables/request-error-handler";
import VehicleSelector from "~/components/vehicle-selector.vue";
import {useI18n} from "vue-i18n";

const api = new ApiRequestSender()
const requestErrorHandler = useRequestErrorHandler()

const {t} = useI18n()
const router = useRouter()

const roomStore = useRoomStore()
const userStore = useUserStore()

const vehicleSelector = ref<InstanceType<typeof VehicleSelector> | undefined>()

const selectedVehicle = ref<string>()
const openedPanels = ref<string[]>(['playersPanel'])

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
    requestErrorHandler.handle(e)
  }
})

watch(() => roomStore.room, value => {
  if (!value) {
    router.push('/rooms')
  }
})

onMounted(() => {
  setSelectedVehicle()
})

function setSelectedVehicle() {
  const memberVehicle = (roomStore.room?.members || [])
      .filter(member => member.nickname === userStore.user!.nickname)
      .map(member => member.selectedVehicle)[0]
  if (memberVehicle && selectedVehicle.value !== memberVehicle) {
    vehicleSelector.value?.setSelectedVehicle(memberVehicle)
  }
}

async function startBattle() {
  try {
    await api.postJson('/rooms/start-battle', {})
  } catch (e) {
    requestErrorHandler.handle(e)
  }
}

async function exit() {
  try {
    await api.delete('/rooms/exit')
    roomStore.room = undefined
    await router.push('/rooms')
  } catch (e) {
    requestErrorHandler.handle(e)
  }
}

function back() {
  router.push('/rooms')
}
</script>

<template>
  <NuxtLayout>
    <v-card width="100%" max-width="600px">
      <v-card-title>
        <menu-navigation/>
      </v-card-title>
      <v-card-text>
        <v-form>
          <vehicle-selector
              ref="vehicleSelector"
              @select="v => selectedVehicle = v"
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
          {{ t('room.battle') }}
        </v-btn>
        <v-expansion-panels class="mb-4" v-model="openedPanels" multiple>
          <v-expansion-panel value="playersPanel">
            <v-expansion-panel-title>
              <v-icon class="mr-2" :icon="mdiAccountMultiple"/>
              {{ t('room.players') }}
            </v-expansion-panel-title>
            <v-expansion-panel-text>
              <room-members-table class="mb-4"/>
            </v-expansion-panel-text>
          </v-expansion-panel>
          <v-expansion-panel v-if="roomStore.userIsRoomOwner" value="invitePlayersPanel">
            <v-expansion-panel-title>
              <v-icon class="mr-2" :icon="mdiAccountPlus"/>
              {{ t('room.invite') }}
            </v-expansion-panel-title>
            <v-expansion-panel-text>
              <online-users-table />
            </v-expansion-panel-text>
          </v-expansion-panel>
        </v-expansion-panels>
        <v-btn class="mb-4" color="warning" width="100%" @click="exit">{{ t('common.exit') }}</v-btn>
        <v-btn class="mb-4" width="100%" @click="back">{{ t('common.back') }}</v-btn>
      </v-card-text>
    </v-card>
  </NuxtLayout>
</template>
