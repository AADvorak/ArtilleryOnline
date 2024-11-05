<script setup lang="ts">
import {useRoomStore} from '~/stores/room'
import {mdiCrown, mdiKnifeMilitary, mdiAccountRemove} from '@mdi/js'
import type {RoomMember} from '~/data/model'
import {ApiRequestSender} from '~/api/api-request-sender'
import {useUserStore} from "~/stores/user";

const api = new ApiRequestSender()

const roomStore = useRoomStore()
const userStore = useUserStore()

const roomMembers = computed(() => {
  return roomStore.room?.members.sort(sortMembers) || []
})

const userIsRoomOwner = computed(() => {
  return roomStore.userIsRoomOwner(userStore.user!)
})

function sortMembers(a: RoomMember, b: RoomMember) {
  if (a.owner) {
    return -1
  }
  return a.nickname > b.nickname ? 1 : -1
}

async function removeUserFromRoom(nickname: string) {
  try {
    await api.delete(`/rooms/guests/${nickname}`)
  } catch (e) {
    console.log(e)
  }
}
</script>

<template>
  <v-table density="compact">
    <thead>
    <tr>
      <th class="text-left">
        Nickname
      </th>
      <th class="text-left">
        Selected vehicle
      </th>
      <th v-if="userIsRoomOwner"></th>
    </tr>
    </thead>
    <tbody>
    <tr v-for="roomMember of roomMembers">
      <td>
        <v-icon :icon="roomMember.owner ? mdiCrown : mdiKnifeMilitary" />
        {{ roomMember.nickname }}
      </td>
      <td>{{ roomMember.selectedVehicle }}</td>
      <td v-if="userIsRoomOwner" class="btn-column">
        <icon-btn
            v-if="!roomMember.owner"
            color="error"
            :icon="mdiAccountRemove"
            tooltip="Remove from room"
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
