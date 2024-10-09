<script setup lang="ts">
import {useRouter} from "#app";

const router = useRouter()

function toBattle() {
  router.push('/battle')
}

function toProfile() {
  router.push('/profile')
}

function signOut() {
  router.push('/')
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
      <v-btn class="mb-4" width="100%" @click="signOut">Sign out</v-btn>
    </v-card-text>
  </v-card>
</template>
