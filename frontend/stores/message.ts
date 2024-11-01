import { ref } from 'vue'
import { defineStore } from 'pinia'
import type {Message, RoomInvitation} from '~/data/model'
import {ApiRequestSender} from '~/api/api-request-sender'

export const useMessageStore = defineStore('message', () => {
  const messages = ref<Message[]>([])
  const roomInvitations = ref<RoomInvitation[]>([])

  async function loadMessages() {
    try {
      messages.value = await new ApiRequestSender()
          .getJson<Message[]>('/messages')
    } catch (e) {
      console.log(e)
    }
  }

  async function loadInvitations() {
    try {
      roomInvitations.value = await new ApiRequestSender()
          .getJson<RoomInvitation[]>('/rooms/invitations')
    } catch (e) {
      console.log(e)
    }
  }

  function addMessage(message: Message) {
    messages.value.unshift(message)
  }

  function removeMessageById(id: string) {
    messages.value = messages.value
        .filter(message => message.id !== id)
  }

  function addRoomInvitation(invitation: RoomInvitation) {
    roomInvitations.value.unshift(invitation)
  }

  function removeRoomInvitationById(id: string) {
    roomInvitations.value = roomInvitations.value
        .filter(invitation => invitation.id !== id)
  }

  return {
    messages,
    roomInvitations,
    loadMessages,
    loadInvitations,
    addMessage,
    removeMessageById,
    addRoomInvitation,
    removeRoomInvitationById
  }
})
