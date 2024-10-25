<script setup lang="ts">
import type {RoomMember} from "~/data/model";
import {useUserStore} from "~/stores/user";

const userStore = useUserStore()

const roomMembers = ref<RoomMember[]>([])

onMounted(() => {
  roomMembers.value.push({
    nickname: userStore.user!.nickname,
    selectedVehicle: ''
  })
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
    </tr>
    </thead>
    <tbody>
    <tr v-for="roomMember of roomMembers">
      <td>{{ roomMember.nickname }}</td>
      <td>{{ roomMember.selectedVehicle }}</td>
    </tr>
    </tbody>
  </v-table>
</template>
