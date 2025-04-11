<script setup lang="ts">
import {useRoomStore} from '~/stores/room'
import {mdiCrown, mdiKnifeMilitary, mdiAccountRemove} from '@mdi/js'
import type {RoomMember} from '~/data/model'
import {ApiRequestSender} from '~/api/api-request-sender'
import {useRequestErrorHandler} from "~/composables/request-error-handler";
import {useI18n} from "vue-i18n";

const {t} = useI18n()
const api = new ApiRequestSender()

const roomStore = useRoomStore()

const roomMembers = computed(() => {
  return roomStore.room?.members.sort(sortMembers) || []
})

function sortMembers(a: RoomMember, b: RoomMember) {
  if (a.owner) {
    return -1
  }
  if (b.owner) {
    return 1
  }
  return a.nickname > b.nickname ? 1 : -1
}

async function removeUserFromRoom(nickname: string) {
  try {
    await api.delete(`/rooms/guests/${nickname}`)
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
        {{ t('common.nickname') }}
      </th>
      <th class="text-left">
        {{ t('roomMembersTable.selectedVehicle') }}
      </th>
      <th v-if="roomStore.userIsRoomOwner"></th>
    </tr>
    </thead>
    <tbody>
    <tr v-for="roomMember of roomMembers">
      <td>
        <v-icon :icon="roomMember.owner ? mdiCrown : mdiKnifeMilitary" />
        {{ roomMember.nickname }}
      </td>
      <td>{{ roomMember.selectedVehicle ? t(`names.vehicles.${roomMember.selectedVehicle}`) : '' }}</td>
      <td v-if="roomStore.userIsRoomOwner" class="btn-column">
        <icon-btn
            v-if="!roomMember.owner"
            color="error"
            :icon="mdiAccountRemove"
            :tooltip="t('roomMembersTable.removeFromRoom')"
            @click="removeUserFromRoom(roomMember.nickname)"
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
