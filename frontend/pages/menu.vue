<script setup lang="ts">
import {useRouter} from "#app";
import {ApiRequestSender} from "~/api/api-request-sender";
import {useUserStore} from "~/stores/user";
import {useStompClientStore} from "~/stores/stomp-client";
import {useI18n} from "vue-i18n";

const {t} = useI18n()
const router = useRouter()
const userStore = useUserStore()
const stompClientStore = useStompClientStore()

function toBattle() {
  router.push('/battle')
}

function toRooms() {
  router.push('/rooms')
}

function toUser() {
  router.push('/user')
}

function toSettings() {
  router.push('/settings')
}

async function logOut() {
  await new ApiRequestSender().delete('/users/logout')
  userStore.user = null
  stompClientStore.disconnect()
  await router.push('/')
}
</script>

<template>
  <NuxtLayout>
    <v-card width="100%" max-width="600px">
      <v-card-title>
        Artillery online: {{ t('menu.title') }}
      </v-card-title>
      <v-card-text>
        <v-btn class="mb-4" width="100%" color="error" @click="toBattle">{{ t('menu.battle') }}</v-btn>
        <v-btn class="mb-4" width="100%" color="primary" @click="toRooms">{{ t('menu.rooms') }}</v-btn>
        <v-btn class="mb-4" width="100%" color="secondary" @click="toUser">{{ t('menu.user') }}</v-btn>
        <v-btn class="mb-4" width="100%" color="secondary" @click="toSettings">{{ t('menu.settings') }}</v-btn>
        <v-btn class="mb-4" width="100%" @click="logOut">{{ t('menu.logOut') }}</v-btn>
      </v-card-text>
    </v-card>
  </NuxtLayout>
</template>
