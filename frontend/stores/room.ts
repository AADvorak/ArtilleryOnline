import {ref} from 'vue'
import {defineStore} from 'pinia'
import {ApiRequestSender} from '~/api/api-request-sender'
import type {Room} from '~/data/model'
import {useUserStore} from '~/stores/user'
import {useStompClientStore} from "~/stores/stomp-client";

export const useRoomStore = defineStore('room', () => {
  const room = ref<Room | undefined>()

  const subscription = ref()

  const userStore = useUserStore()
  const stompClientStore = useStompClientStore()

  const allMembers = computed(() => {
    return room.value?.members.reduce((p, c) => [...p, ...c], []) || []
  })

  const userIsRoomOwner = computed(() => {
    const members = allMembers.value
    const ownerNickname = members.filter(member => member.owner).map(member => member.nickname)[0]
    const userNickname = userStore.user?.nickname
    return ownerNickname === userNickname
  })

  watch(room, value => {
    value ? subscribeToRoomUpdates() : unsubscribeFromRoomUpdates()
  })

  async function loadRoomIfNull() {
    if (!room.value) {
      try {
        room.value = await new ApiRequestSender().getJson<Room>('/rooms/my')
      } catch (e) {
        console.log(e)
      }
    }
  }

  function subscribeToRoomUpdates() {
    if (!subscription.value) {
      subscription.value = stompClientStore.client!.subscribe('/user/topic/room/updates', function (msgOut) {
        room.value = JSON.parse(msgOut.body) as Room
        if (room.value.deleted) {
          room.value = undefined
        }
      })
    }
  }

  function unsubscribeFromRoomUpdates() {
    if (subscription.value) {
      subscription.value.unsubscribe()
      subscription.value = null
    }
  }

  function clear() {
    room.value = undefined
  }

  return {room, allMembers, loadRoomIfNull, userIsRoomOwner, clear}
})
