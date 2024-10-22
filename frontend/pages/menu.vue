<script setup lang="ts">
import {useRouter} from "#app";
import {ApiRequestSender} from "~/api/api-request-sender";
import {useUserStore} from "~/stores/user";

const router = useRouter()
const userStore = useUserStore()

function toBattle() {
  router.push('/battle')
}

function toProfile() {
  router.push('/profile')
}

async function logOut() {
  await new ApiRequestSender().delete('/users/logout')
  userStore.user = null
  await router.push('/')
}
</script>

<template>
  <v-card width="100%" max-width="600px">
    <v-card-title>
      Artillery online: menu
    </v-card-title>
    <v-card-text>
      <v-btn class="mb-4" width="100%" color="error" @click="toBattle">Battle</v-btn>
      <v-btn class="mb-4" width="100%" color="secondary" @click="toProfile">Profile</v-btn>
      <v-btn class="mb-4" width="100%" @click="logOut">Log out</v-btn>
    </v-card-text>
  </v-card>
</template>
