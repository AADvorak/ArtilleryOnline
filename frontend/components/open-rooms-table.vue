<script setup lang="ts">
import {mdiLogin} from '@mdi/js'
import {ApiRequestSender} from '~/api/api-request-sender'
import {useRequestErrorHandler} from "~/composables/request-error-handler";
import {useI18n} from "vue-i18n";
import type {Room, RoomShort} from "~/data/model";
import {useRoomStore} from "~/stores/room";
import {useRouter} from "#app";

const {t} = useI18n()
const api = new ApiRequestSender()
const router = useRouter()
const roomStore = useRoomStore()
const settingsStore = useSettingsStore()

const rooms = ref<RoomShort[]>([])

const maxRoomMembers = computed(() => {
  return settingsStore.limits!.maxRoomMembers
})

onMounted(() => {
  loadRooms()
})

async function loadRooms() {
  try {
    rooms.value = await api.getJson('/rooms')
  } catch (e) {
    useRequestErrorHandler().handle(e)
  }
}

async function enterRoom(id: string) {
  try {
    roomStore.room = await api.putJson<undefined, Room>(`/rooms/${id}/enter`, undefined)
    await router.push('/rooms/room')
  } catch (e) {
    useRequestErrorHandler().handle(e)
  }
}
</script>

<template>
  <v-table density="compact">
    <thead>
    <tr>
      <th class="text-left">
        {{ t('rooms.owner') }}
      </th>
      <th class="text-left">
        {{ t('rooms.membersCount') }}
      </th>
      <th class="text-left">
        {{ t('rooms.inBattle') }}
      </th>
      <th></th>
    </tr>
    </thead>
    <tbody>
    <tr v-if="!rooms.length" style="text-align: center;">{{ t('rooms.noRooms') }}</tr>
    <tr v-for="room of rooms">
      <td>{{ room.ownerNickname }}</td>
      <td>{{ room.membersCount }} / {{ maxRoomMembers }}</td>
      <td>{{ room.inBattle ? t('common.yes') : t('common.no') }}</td>
      <td class="btn-column">
        <icon-btn
            color="success"
            :icon="mdiLogin"
            :tooltip="t('rooms.enter')"
            :disabled="room.membersCount >= maxRoomMembers"
            @click="enterRoom(room.id)"
        />
      </td>
    </tr>
    </tbody>
  </v-table>
</template>

<style scoped>
.btn-column {
  text-align: right;
}
</style>
