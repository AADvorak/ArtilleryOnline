import { ref } from 'vue'
import { defineStore } from 'pinia'
import type {RoomInvitation} from '~/data/model'
import {ApiRequestSender} from '~/api/api-request-sender'

export const useMessageStore = defineStore('message', () => {
  const roomInvitations = ref<RoomInvitation[]>([])

  async function loadInvitations() {
    try {
      roomInvitations.value = await new ApiRequestSender()
          .getJson<RoomInvitation[]>('/rooms/invitations')
    } catch (e) {
      console.log(e)
    }
  }

  function add(invitation: RoomInvitation) {
    roomInvitations.value.push(invitation)
  }

  function removeById(id: string) {
    roomInvitations.value = roomInvitations.value
        .filter(invitation => invitation.id !== id)
  }

  return { roomInvitations, loadInvitations, add, removeById }
})
