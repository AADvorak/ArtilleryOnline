import { ref } from 'vue'
import { defineStore } from 'pinia'
import type {RoomInvitation} from '~/data/model'

export const useMessageStore = defineStore('message', () => {
  const roomInvitations = ref<RoomInvitation[]>([])

  function add(invitation: RoomInvitation) {
    roomInvitations.value.push(invitation)
  }

  function removeById(id: string) {
    roomInvitations.value = roomInvitations.value
        .filter(invitation => invitation.id !== id)
  }

  return { roomInvitations, add, removeById }
})
