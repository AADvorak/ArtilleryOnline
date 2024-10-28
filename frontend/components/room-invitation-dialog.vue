<script setup lang="ts">
import {useRouter} from "#app";
import {ref} from "vue";
import type {Room, RoomInvitation} from "~/data/model";
import {useStompClientStore} from "~/stores/stomp-client";
import {ApiRequestSender} from "~/api/api-request-sender";
import type {EnterRoomRequest} from "~/data/request";
import {useRoomStore} from "~/stores/room";
import {useUserStore} from "~/stores/user";

const router = useRouter()

const stompClientStore = useStompClientStore()
const roomStore = useRoomStore()
const userStore = useUserStore()

const opened = ref(false)
const invitation = ref<RoomInvitation>()
const subscription = ref()

watch(() => userStore.user, (value) => {
  !!value ? subscribe() : unsubscribe()
})

onMounted(() => {
  if (!!userStore.user) {
    subscribe()
  }
})

onBeforeUnmount(() => {
  unsubscribe()
})

function subscribe() {
  subscription.value = stompClientStore.client!.subscribe('/user/topic/room/invitations', function (msgOut) {
    invitation.value = JSON.parse(msgOut.body) as RoomInvitation
    opened.value = true
  })
}

function unsubscribe() {
  subscription.value && subscription.value.unsubscribe()
}

async function acceptInvitation() {
  try {
    roomStore.room = await new ApiRequestSender().postJson<EnterRoomRequest, Room>('/rooms/enter', {
      invitationId: invitation.value!.id
    })
    opened.value = false
    await router.push('/rooms/room')
  } catch (e) {
    console.log(e)
  }
}

async function declineInvitation() {
  opened.value = false
}
</script>

<template>
  <v-dialog :model-value="opened" :persistent="true" max-width="600px">
    <v-card width="100%">
      <v-card-title>Invitation to room</v-card-title>
      <v-card-text>
        <div class="d-flex">You have received an invitation from {{ invitation.inviterNickname }}.</div>
        <div class="d-flex mt-4">
          <v-btn color="success" @click="acceptInvitation">Enter room</v-btn>
          <v-btn @click="declineInvitation">Cancel</v-btn>
        </div>
      </v-card-text>
    </v-card>
  </v-dialog>
</template>
