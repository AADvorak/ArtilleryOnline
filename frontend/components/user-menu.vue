<script setup lang="ts">
import {ref} from "vue";
import { mdiAccount } from '@mdi/js'
import {useI18n} from "vue-i18n";
import {useUserStore} from "~/stores/user";
import {ApiRequestSender} from "~/api/api-request-sender";
import {useStompClientStore} from "~/stores/stomp-client";
import {useRouter} from "#app";

const {t} = useI18n()
const userStore = useUserStore()
const stompClientStore = useStompClientStore()
const router = useRouter()

const opened = ref(false)

async function logOut() {
  await new ApiRequestSender().delete('/users/logout')
  userStore.user = undefined
  stompClientStore.disconnect()
  await router.push('/')
}
</script>

<template>
  <v-menu
      v-model="opened"
      close-on-content-click
      location="bottom"
  >
    <template v-slot:activator="{ props }">
      <v-btn
          v-bind="props"
      >
        <v-icon :icon="mdiAccount" />
        {{ userStore.user?.nickname }}
      </v-btn>
    </template>
    <v-card>
      <v-btn class="mt-2" width="100%" @click="logOut">{{ t('menu.logOut') }}</v-btn>
    </v-card>
  </v-menu>
</template>
