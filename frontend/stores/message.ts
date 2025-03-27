import { ref } from 'vue'
import { defineStore } from 'pinia'
import type {Message, RoomInvitation} from '~/data/model'
import {ApiRequestSender} from '~/api/api-request-sender'
import {useStompClientStore} from "~/stores/stomp-client";
import type {StompSubscription} from "@stomp/stompjs";

export const useMessageStore = defineStore('message', () => {
  const messages = ref<Message[]>()
  const roomInvitations = ref<RoomInvitation[]>()

  const stompClientStore = useStompClientStore()

  const subscriptions = ref<StompSubscription[]>([])

  async function loadMessagesIfNull() {
    if (!messages.value) {
      try {
        messages.value = await new ApiRequestSender()
            .getJson<Message[]>('/messages')
      } catch (e) {
        console.log(e)
      }
    }
  }

  async function loadInvitationsIfNull() {
    if (!roomInvitations.value) {
      try {
        roomInvitations.value = await new ApiRequestSender()
            .getJson<RoomInvitation[]>('/rooms/invitations')
      } catch (e) {
        console.log(e)
      }
    }
  }

  function addMessage(message: Message) {
    messages.value && messages.value.unshift(message)
  }

  function removeMessageById(id: string) {
    if (messages.value) {
      messages.value = messages.value
          .filter(message => message.id !== id)
    }
  }

  function addRoomInvitation(invitation: RoomInvitation) {
    roomInvitations.value && roomInvitations.value.unshift(invitation)
  }

  function removeRoomInvitationById(id: string) {
    if (roomInvitations.value) {
      roomInvitations.value = roomInvitations.value
          .filter(invitation => invitation.id !== id)
    }
  }

  function subscribe() {
    if (!subscriptions.value.length) {
      subscriptions.value.push(stompClientStore.client!.subscribe('/user/topic/messages', function (msgOut) {
        addMessage(JSON.parse(msgOut.body) as Message)
      }))
      subscriptions.value.push(stompClientStore.client!.subscribe('/user/topic/room/invitations', function (msgOut) {
        addRoomInvitation(JSON.parse(msgOut.body) as RoomInvitation)
      }))
    }
  }

  function clear() {
    messages.value = undefined
    roomInvitations.value = undefined
  }

  return {
    messages,
    roomInvitations,
    subscribe,
    loadMessagesIfNull,
    loadInvitationsIfNull,
    removeMessageById,
    removeRoomInvitationById,
    clear
  }
})
