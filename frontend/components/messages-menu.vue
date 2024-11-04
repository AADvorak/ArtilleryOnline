<script setup lang="ts">
import {ref} from "vue";
import {useMessageStore} from "~/stores/message";
import type {Message, RoomInvitation} from "~/data/model";
import {useUserStore} from "~/stores/user";
import {useStompClientStore} from "~/stores/stomp-client";
import MessageCard from "~/components/message-card.vue";
import { mdiMessage } from '@mdi/js'

const stompClientStore = useStompClientStore()
const messageStore = useMessageStore()
const userStore = useUserStore()

const opened = ref(false)
const subscriptions = ref([])

const messages = computed(() => {
  return messageStore.messages
})
const roomInvitations = computed(() => {
  return messageStore.roomInvitations
})

watch(() => userStore.user, (value) => {
  !!value ? subscribe() : unsubscribe()
})

onMounted(() => {
  if (!!userStore.user) {
    subscribe()
  }
})

onBeforeUnmount(() => {
  unsubscribe()
})

function subscribe() {
  subscriptions.value.push(stompClientStore.client!.subscribe('/user/topic/messages', function (msgOut) {
    messageStore.addMessage(JSON.parse(msgOut.body) as Message)
    opened.value = true
  }))
  subscriptions.value.push(stompClientStore.client!.subscribe('/user/topic/room/invitations', function (msgOut) {
    messageStore.addRoomInvitation(JSON.parse(msgOut.body) as RoomInvitation)
    opened.value = true
  }))
}

function unsubscribe() {
  subscriptions.value.forEach(subscription => subscription.unsubscribe())
}
</script>

<template>
  <v-menu
      v-model="opened"
      close-on-content-click
      location="bottom"
  >
    <template v-slot:activator="{ props }">
      <v-btn :color="!!roomInvitations.length || !!messages.length ? 'primary' : ''" v-bind="props">
        <v-icon :icon="mdiMessage" />
        <v-tooltip
            activator="parent"
            location="bottom"
            open-delay="1000">
          Messages
        </v-tooltip>
      </v-btn>
    </template>
    <v-card min-width="300">
      <div class="no-messages-div" v-if="!roomInvitations.length && !messages.length">You have no messages</div>
      <v-list v-else>
        <v-list-item v-for="invitation in roomInvitations">
          <room-invitation-card :invitation="invitation" />
        </v-list-item>
        <v-list-item v-for="message in messages">
          <message-card :message="message" />
        </v-list-item>
      </v-list>
    </v-card>
  </v-menu>
</template>

<style scoped>
.no-messages-div {
  height: 50px;
}
</style>
