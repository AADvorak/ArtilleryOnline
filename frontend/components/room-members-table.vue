<script setup lang="ts">
import {useRoomStore} from "~/stores/room";

const roomStore = useRoomStore()

const roomMembers = computed(() => {
  return roomStore.room?.members || []
})
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
      <th class="text-left">
        Type
      </th>
    </tr>
    </thead>
    <tbody>
    <tr v-for="roomMember of roomMembers">
      <td>{{ roomMember.nickname }}</td>
      <td>{{ roomMember.selectedVehicle }}</td>
      <td>{{ roomMember.owner ? 'owner' : 'guest' }}</td>
    </tr>
    </tbody>
  </v-table>
</template>
