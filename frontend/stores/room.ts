import {ref} from 'vue'
import {defineStore} from 'pinia'
import {ApiRequestSender} from '~/api/api-request-sender'
import type {Room} from '~/data/model'

export const useRoomStore = defineStore('room', () => {
  const room = ref<Room>()

  async function loadRoomIfNull() {
    if (!room.value) {
      try {
        room.value = await new ApiRequestSender().getJson<Room>('/rooms')
      } catch (e) {
        console.log(e)
      }
    }
  }

  return {room, loadRoomIfNull}
})
