<script setup lang="ts">
import {useRouter} from "#app";

const router = useRouter()

function signIn() {
  router.push('/sign-in')
}

function signUp() {
  router.push('/sign-up')
}
</script>

<template>
  <v-card width="100%" max-width="600px">
    <v-card-title>
      Artillery online
    </v-card-title>
    <v-card-text>
      <v-btn class="mb-4" width="100%" color="primary" @click="signIn">Sign in</v-btn>
      <v-btn class="mb-4" width="100%" color="secondary" @click="signUp">Sign up</v-btn>
    </v-card-text>
  </v-card>
</template>
