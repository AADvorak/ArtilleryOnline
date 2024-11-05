import {ref} from 'vue'
import {defineStore} from 'pinia'
import {ApiRequestSender} from '~/api/api-request-sender'
import type {Room, User} from '~/data/model'

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

  function userIsRoomOwner(user: User) {
    const members = room.value?.members || []
    const ownerNickname = members.filter(member => member.owner).map(member => member.nickname)[0]
    const userNickname = user.nickname
    return ownerNickname === userNickname
  }

  return {room, loadRoomIfNull, userIsRoomOwner}
})
