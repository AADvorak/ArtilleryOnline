import {ref} from 'vue'
import {defineStore} from 'pinia'
import {ApiRequestSender} from '~/api/api-request-sender'
import type {ChatMessage, Room} from '~/data/model'
import {useUserStore} from '~/stores/user'
import {useStompClientStore} from "~/stores/stomp-client";

export const useRoomStore = defineStore('room', () => {

  const room = ref<Room | undefined>()

  const messages = ref<ChatMessage[]>([])

  const updatesSubscription = ref()

  const messagesSubscription = ref()

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
    if (value) {
      subscribe()
      loadMessages().then()
    } else {
      unsubscribe()
    }
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

  async function loadMessages() {
    try {
      messages.value = await new ApiRequestSender().getJson<ChatMessage[]>('/rooms/my/messages')
    } catch (e) {
      console.log(e)
    }
  }

  function subscribe() {
    if (!updatesSubscription.value) {
      updatesSubscription.value = stompClientStore.client!.subscribe('/user/topic/room/updates', function (msgOut) {
        room.value = JSON.parse(msgOut.body) as Room
        if (room.value.deleted) {
          room.value = undefined
        }
      })
    }
    if (!messagesSubscription.value) {
      messagesSubscription.value = stompClientStore.client!.subscribe('/user/topic/room/messages', function (msgOut) {
        messages.value.push(JSON.parse(msgOut.body) as ChatMessage)
      })
    }
  }

  function unsubscribe() {
    if (updatesSubscription.value) {
      updatesSubscription.value.unsubscribe()
      updatesSubscription.value = null
    }
    if (messagesSubscription.value) {
      messagesSubscription.value.unsubscribe()
      messagesSubscription.value = null
    }
  }

  function clear() {
    room.value = undefined
    messages.value = []
  }

  return {room, messages, allMembers, loadRoomIfNull, userIsRoomOwner, clear}
})
