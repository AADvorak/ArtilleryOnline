<script setup lang="ts">
import type {User} from "~/data/model";
import {ApiRequestSender} from "~/api/api-request-sender";
import {useRoomStore} from "~/stores/room";

const roomStore = useRoomStore()

const onlineUsers = ref<User[]>([])
const invitedUsers = ref<User[]>([])

const roomMemberNicknames = computed(() => {
  const members = roomStore.room?.members || []
  return members.map(member => member.nickname)
})

onMounted(() => {
  loadOnlineUsers()
})

async function loadOnlineUsers() {
  try {
    const users = await new ApiRequestSender().getJson<User[]>('/users/online')
    onlineUsers.value = users.filter(user => !roomMemberNicknames.value.includes(user.nickname))
  } catch (e) {
    console.log(e)
  }
}

async function inviteUser(user) {
  try {
    await new ApiRequestSender().postJson('/rooms/invite', user)
    invitedUsers.value.push(user)
  } catch (e) {
    console.log(e)
  }
}

function userIsInvited(user) {
  for (const invitedUser of invitedUsers.value) {
    if (user.nickname === invitedUser.nickname) {
      return true
    }
  }
  return false
}
</script>

<template>
  <v-table density="compact">
    <div v-if="!onlineUsers.length">
      No available users
    </div>
    <tbody v-else>
    <tr v-for="onlineUser of onlineUsers">
      <td>{{ onlineUser.nickname }}</td>
      <td>
        <template v-if="userIsInvited(onlineUser)">Invitation sent</template>
        <v-btn v-else variant="text" @click="inviteUser(onlineUser)">Invite</v-btn>
      </td>
    </tr>
    </tbody>
  </v-table>
</template>
