<script setup lang="ts">
import type {Room, RoomInvitation} from '~/data/model'
import {useMessageStore} from '~/stores/message'
import {ApiRequestSender} from "~/api/api-request-sender";
import {useRoomStore} from "~/stores/room";
import {useRouter} from "#app";
import {useRequestErrorHandler} from "~/composables/request-error-handler";

const props = defineProps<{
  invitation: RoomInvitation
}>()

const router = useRouter()

const messageStore = useMessageStore()
const roomStore = useRoomStore()

async function acceptInvitation() {
  const invitationId = props.invitation!.id
  try {
    roomStore.room = await new ApiRequestSender()
        .postJson<undefined, Room>(`/rooms/invitations/${invitationId}/accept`, undefined)
    await router.push('/rooms/room')
  } catch (e) {
    if (e.status === 404) {
      e.error = {message: 'The invitation is outdated'}
    }
    useRequestErrorHandler().handle(e)
  } finally {
    messageStore.removeRoomInvitationById(invitationId)
  }
}

async function deleteInvitation() {
  const invitationId = props.invitation!.id
  try {
    await new ApiRequestSender().delete(`/rooms/invitations/${invitationId}`)
  } catch (e) {
    useRequestErrorHandler().handle(e)
  } finally {
    messageStore.removeRoomInvitationById(invitationId)
  }
}
</script>

<template>
  <v-card width="100%">
    <v-card-text>
      <div class="d-flex">You have received an invitation from {{ invitation.inviterNickname }}.</div>
      <div class="d-flex mt-4">
        <v-btn color="success" @click="acceptInvitation">Enter room</v-btn>
        <v-btn @click="deleteInvitation">Cancel</v-btn>
      </div>
    </v-card-text>
  </v-card>
</template>
