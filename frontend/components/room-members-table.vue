<script setup lang="ts">
import {useRoomStore} from '~/stores/room'
import {mdiCrown, mdiKnifeMilitary} from '@mdi/js'
import type {RoomMember} from '~/data/model'

const roomStore = useRoomStore()

const roomMembers = computed(() => {
  return roomStore.room?.members.sort(sortMembers) || []
})

function sortMembers(a: RoomMember, b: RoomMember) {
  if (a.owner) {
    return -1
  }
  return a.nickname > b.nickname ? 1 : -1
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
    </tr>
    </thead>
    <tbody>
    <tr v-for="roomMember of roomMembers">
      <td>
        <v-icon :icon="roomMember.owner ? mdiCrown : mdiKnifeMilitary" />
        {{ roomMember.nickname }}
      </td>
      <td>{{ roomMember.selectedVehicle }}</td>
    </tr>
    </tbody>
  </v-table>
</template>
