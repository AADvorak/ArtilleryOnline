<script setup lang="ts">
import type {User} from "~/data/model";
import {ApiRequestSender} from "~/api/api-request-sender";

const onlineUsers = ref<User[]>([])

onMounted(() => {
  loadOnlineUsers()
})

async function loadOnlineUsers() {
  try {
    onlineUsers.value = await new ApiRequestSender().getJson<User[]>('/users/online')
  } catch (e) {
    console.log(e)
  }
}

async function inviteUser(user) {
  try {
    await new ApiRequestSender().postJson('/rooms/invite', user)
  } catch (e) {
    console.log(e)
  }
}
</script>

<template>
  <v-table density="compact">
    <tbody>
    <tr v-for="onlineUser of onlineUsers">
      <td>{{ onlineUser.nickname }}</td>
      <td><v-btn variant="text" @click="inviteUser(onlineUser)">Invite</v-btn></td>
    </tr>
    </tbody>
  </v-table>
</template>
