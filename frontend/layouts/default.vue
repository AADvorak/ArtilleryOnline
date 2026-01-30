<script setup lang="ts">
import MessagesMenu from "~/components/messages-menu.vue";
import UserMenu from "~/components/user-menu.vue";
import {useStompClientStore} from "~/stores/stomp-client";
import {ref} from "vue";
import type {StompSubscription} from "@stomp/stompjs";
import {useRouter} from "#app";
import {useQueueStore} from "~/stores/queue";
import FullScreenBtn from "~/components/full-screen-btn.vue";
import BattleBtn from "~/components/battle-btn.vue";

const stompClientStore = useStompClientStore()
const queueStore = useQueueStore()
const router = useRouter()

const subscriptions = ref<StompSubscription[]>([])

onMounted(() => {
  subscribe()
})

function subscribe() {
  if (!subscriptions.value.length) {
    subscriptions.value.push(stompClientStore.client!.subscribe('/user/topic/battle-started', function () {
      queueStore.queue = undefined
      router.push('/playground')
    }))
  }
}
</script>

<template>
  <v-app full-height theme="dark">
    <v-app-bar density="compact">
      <server-counts />
      <battle-btn />
      <v-spacer />
      <messages-menu />
      <room-menu />
      <full-screen-btn />
      <user-menu />
    </v-app-bar>
    <div class="d-flex align-center justify-center flex-column default">
      <slot />
    </div>
    <error-message />
    <connection-lost-dialog />
    <locale />
  </v-app>
</template>

<style scoped>
.default {
  padding-top: 48px;
  height: 100%;
}
</style>
