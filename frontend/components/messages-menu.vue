<script setup lang="ts">
import {ref} from "vue";
import {useMessageStore} from "~/stores/message";
import type {RoomInvitation} from "~/data/model";
import {useUserStore} from "~/stores/user";
import {useStompClientStore} from "~/stores/stomp-client";

const stompClientStore = useStompClientStore()
const messageStore = useMessageStore()
const userStore = useUserStore()

const opened = ref(false)
const subscription = ref()

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
  subscription.value = stompClientStore.client!.subscribe('/user/topic/room/invitations', function (msgOut) {
    messageStore.add(JSON.parse(msgOut.body) as RoomInvitation)
    opened.value = true
  })
}

function unsubscribe() {
  subscription.value && subscription.value.unsubscribe()
}
</script>

<template>
  <v-menu
      v-model="opened"
      close-on-content-click
      location="bottom"
  >
    <template v-slot:activator="{ props }">
      <v-btn :color="!!roomInvitations.length ? 'primary' : ''" v-bind="props">
        Messages
      </v-btn>
    </template>
    <v-card min-width="300">
      <div class="no-messages-div" v-if="!roomInvitations.length">You have no messages</div>
      <v-list v-else>
        <v-list-item v-for="invitation in roomInvitations">
          <room-invitation-card :invitation="invitation" />
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
