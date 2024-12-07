<script setup lang="ts">
import type {User} from "~/data/model";
import {ApiRequestSender} from "~/api/api-request-sender";
import {useRoomStore} from "~/stores/room";
import {useRequestErrorHandler} from "~/composables/request-error-handler";
import {useI18n} from "vue-i18n";

const {t} = useI18n()
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
    useRequestErrorHandler().handle(e)
  }
}

async function inviteUser(user) {
  try {
    await new ApiRequestSender().postJson('/rooms/invitations', user)
    invitedUsers.value.push(user)
  } catch (e) {
    useRequestErrorHandler().handle(e)
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
      {{ t('onlineUsersTable.noAvailableUsers') }}
    </div>
    <tbody v-else>
    <tr v-for="onlineUser of onlineUsers">
      <td>{{ onlineUser.nickname }}</td>
      <td class="btn-column">
        <template v-if="userIsInvited(onlineUser)">{{ t('onlineUsersTable.invitationSent') }}</template>
        <v-btn v-else variant="text" @click="inviteUser(onlineUser)">{{ t('onlineUsersTable.invite') }}</v-btn>
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

