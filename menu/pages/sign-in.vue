<script setup lang="ts">
import {useRouter} from "#app";

const router = useRouter()

function signIn() {
  router.push('/menu')
}

function signUp() {
  router.push('/sign-up')
}
</script>

<template>
  <v-card width="100%" max-width="600px">
    <v-card-title>
      Artillery online: sign in
    </v-card-title>
    <v-card-text>
      <v-form @submit.prevent="signIn">
        <v-text-field label="Email"/>
        <v-text-field label="Password"/>
      </v-form>
      <v-btn class="mb-4" width="100%" color="primary" @click="signIn">Sign in</v-btn>
      <v-btn class="mb-4" width="100%" color="secondary" @click="signUp">Sign up</v-btn>
    </v-card-text>
  </v-card>
</template>
