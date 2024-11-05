import {ref} from 'vue'
import {defineStore} from 'pinia'
import {ApiRequestSender} from '~/api/api-request-sender'
import type {Room} from '~/data/model'
import {useUserStore} from "~/stores/user";

export const useRoomStore = defineStore('room', () => {
  const room = ref<Room>()

  const userStore = useUserStore()

  const userIsRoomOwner = computed(() => {
    const members = room.value?.members || []
    const ownerNickname = members.filter(member => member.owner).map(member => member.nickname)[0]
    const userNickname = userStore.user?.nickname
    return ownerNickname === userNickname
  })

  async function loadRoomIfNull() {
    if (!room.value) {
      try {
        room.value = await new ApiRequestSender().getJson<Room>('/rooms')
      } catch (e) {
        console.log(e)
      }
    }
  }

  return {room, loadRoomIfNull, userIsRoomOwner}
})
