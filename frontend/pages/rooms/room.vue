<script setup lang="ts">
import {useRouter} from "#app";
import {ApiRequestSender} from "~/api/api-request-sender";
import {useRoomStore} from "~/stores/room";
import {useUserStore} from "~/stores/user";
import {mdiAccountMultiple, mdiAccountPlus, mdiMessageTextOutline, mdiRobot} from '@mdi/js'
import {useRequestErrorHandler} from "~/composables/request-error-handler";
import VehicleSelector from "~/components/vehicle-selector.vue";
import {useI18n} from "vue-i18n";
import RoomMembersTable from "~/components/room-members-table.vue";
import {BattleType} from "~/playground/data/battle";
import RoomMessenger from "~/components/room-messenger.vue";
import {ref} from "vue";

const ExpansionPanels = {
  PLAYERS: 'playersPanel',
  MESSENGER: 'messengerPanel',
  INVITE_PLAYERS: 'invitePlayersPanel',
  ADD_BOTS: 'addBotsPanel',
}

const api = new ApiRequestSender()
const requestErrorHandler = useRequestErrorHandler()

const {t} = useI18n()
const router = useRouter()

const roomStore = useRoomStore()
const userStore = useUserStore()

const vehicleSelector = ref<InstanceType<typeof VehicleSelector> | undefined>()
const messengerBottomAnchor = ref<HTMLElement>()

const selectedVehicle = ref<string>()
const openedPanels = ref<string[]>([ExpansionPanels.PLAYERS])
const availableBattleTypes = ref<BattleType[]>([BattleType.DEATHMATCH, BattleType.TEAM_ELIMINATION])
const battleType = ref<BattleType | undefined>()

const readyToBattle = computed(() => {
  const members = roomStore.allMembers
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
    await api.putJson('/rooms/my/select-vehicle', {
      selectedVehicle: value
    })
  } catch (e) {
    requestErrorHandler.handle(e)
  }
})

watch(() => roomStore.room, value => {
  if (!value) {
    router.push('/rooms')
  } else {
    battleType.value = value.battleType
  }
})

watch(battleType, (value, oldValue) => {
  value && oldValue && value !== oldValue && changeBattleType(value)
})

watch(openedPanels, (value, oldValue) => {
  if (value.includes(ExpansionPanels.MESSENGER) && !oldValue.includes(ExpansionPanels.MESSENGER)) {
    scrollToMessengerBottomAnchor()
  }
})

onMounted(() => {
  if (roomStore.room) {
    battleType.value = roomStore.room.battleType
  }
  setSelectedVehicle()
})

function scrollToMessengerBottomAnchor() {
  setTimeout(() => {
    messengerBottomAnchor.value && messengerBottomAnchor.value.scrollIntoView({behavior: 'smooth'})
  }, 200)
}

function setSelectedVehicle() {
  const memberVehicle = roomStore.allMembers
      .filter(member => member.nickname === userStore.user!.nickname)
      .map(member => member.selectedVehicle)[0]
  if (memberVehicle && selectedVehicle.value !== memberVehicle) {
    vehicleSelector.value?.setSelectedVehicle(memberVehicle)
  }
}

async function startBattle() {
  try {
    await api.postJson('/rooms/my/start-battle', {})
  } catch (e) {
    requestErrorHandler.handle(e)
  }
}

async function addBot() {
  try {
    await api.postJson('/rooms/my/bots', {})
  } catch (e) {
    requestErrorHandler.handle(e)
  }
}

async function changeOpen() {
  try {
    await api.putJson('/rooms/my/open', {on: !roomStore.room?.open})
  } catch (e) {
    requestErrorHandler.handle(e)
  }
}

async function changeBattleType(battleType: BattleType) {
  try {
    await api.putJson('/rooms/my/battle-type', {battleType})
  } catch (e) {
    requestErrorHandler.handle(e)
  }
}

async function exit() {
  try {
    await api.delete('/rooms/my/exit')
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
        <v-toolbar color="transparent">
          <battle-types-selector
              v-model="battleType"
              :available-types="availableBattleTypes"
              :disabled="!roomStore.userIsRoomOwner"
          />
          <v-checkbox
              density="compact"
              class="ml-4"
              :model-value="roomStore.room?.open"
              :label="t('room.open')"
              :disabled="!roomStore.userIsRoomOwner"
              @click="changeOpen"
          />
        </v-toolbar>
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
          <v-expansion-panel :value="ExpansionPanels.PLAYERS">
            <v-expansion-panel-title>
              <v-icon class="mr-2" :icon="mdiAccountMultiple"/>
              {{ t('room.players') }}
            </v-expansion-panel-title>
            <v-expansion-panel-text>
              <room-members-table class="mb-4"/>
            </v-expansion-panel-text>
          </v-expansion-panel>
          <v-expansion-panel :value="ExpansionPanels.MESSENGER">
            <v-expansion-panel-title>
              <v-badge
                  v-if="roomStore.newMessagesCount > 0"
                  class="mr-2"
                  color="error"
                  :content="roomStore.newMessagesCount"
              >
                <v-icon :icon="mdiMessageTextOutline"/>
              </v-badge>
              <v-icon v-else class="mr-2" :icon="mdiMessageTextOutline"/>
              {{ t('messenger.messages') }}
            </v-expansion-panel-title>
            <v-expansion-panel-text>
              <room-messenger class="mb-4"/>
              <div ref="messengerBottomAnchor"></div>
            </v-expansion-panel-text>
          </v-expansion-panel>
          <v-expansion-panel v-if="roomStore.userIsRoomOwner" :value="ExpansionPanels.INVITE_PLAYERS">
            <v-expansion-panel-title>
              <v-icon class="mr-2" :icon="mdiAccountPlus"/>
              {{ t('room.invite') }}
            </v-expansion-panel-title>
            <v-expansion-panel-text>
              <online-users-table />
            </v-expansion-panel-text>
          </v-expansion-panel>
          <v-expansion-panel v-if="roomStore.userIsRoomOwner" :value="ExpansionPanels.ADD_BOTS">
            <v-expansion-panel-title>
              <v-icon class="mr-2" :icon="mdiRobot"/>
              {{ t('room.addBots') }}
            </v-expansion-panel-title>
            <v-expansion-panel-text>
              <v-btn
                  color="success"
                  @click="addBot"
              >
                {{ t('room.addBot') }}
              </v-btn>
            </v-expansion-panel-text>
          </v-expansion-panel>
        </v-expansion-panels>
        <v-btn class="mb-4" color="warning" width="100%" @click="exit">{{ t('common.exit') }}</v-btn>
        <v-btn class="mb-4" width="100%" @click="back">{{ t('common.back') }}</v-btn>
      </v-card-text>
    </v-card>
  </NuxtLayout>
</template>
