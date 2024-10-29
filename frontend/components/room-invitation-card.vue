<script setup lang="ts">
import type {Room, RoomInvitation} from '~/data/model'
import {useMessageStore} from '~/stores/message'
import {ApiRequestSender} from "~/api/api-request-sender";
import type {EnterRoomRequest} from "~/data/request";
import {useRoomStore} from "~/stores/room";
import {useRouter} from "#app";

const props = defineProps<{
  invitation: RoomInvitation
}>()

const router = useRouter()

const messageStore = useMessageStore()
const roomStore = useRoomStore()

async function acceptInvitation() {
  try {
    const invitationId = props.invitation!.id
    roomStore.room = await new ApiRequestSender().postJson<EnterRoomRequest, Room>('/rooms/enter', {
      invitationId
    })
    messageStore.removeById(invitationId)
    await router.push('/rooms/room')
  } catch (e) {
    console.log(e)
  }
}

async function declineInvitation() {
  messageStore.removeById(props.invitation!.id)
}
</script>

<template>
  <v-card width="100%">
    <v-card-text>
      <div class="d-flex">You have received an invitation from {{ invitation.inviterNickname }}.</div>
      <div class="d-flex mt-4">
        <v-btn color="success" @click="acceptInvitation">Enter room</v-btn>
        <v-btn @click="declineInvitation">Cancel</v-btn>
      </div>
    </v-card-text>
  </v-card>
</template>
