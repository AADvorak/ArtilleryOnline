<script setup lang="ts">
import {useRouter} from "#app";

const router = useRouter()

function signIn() {
  router.push('/sign-in')
}

function signUp() {
  router.push('/menu')
}
</script>

<template>
  <v-card width="100%" max-width="600px">
    <v-card-title>
      Artillery online: sign up
    </v-card-title>
    <v-card-text>
      <v-form @submit.prevent="signUp">
        <v-text-field label="Email"/>
        <v-text-field label="Password"/>
        <v-text-field label="Password confirm"/>
        <v-text-field label="Nickname"/>
      </v-form>
      <v-btn class="mb-4" width="100%" color="primary" @click="signUp">Sign up</v-btn>
      <v-btn class="mb-4" width="100%" color="secondary" @click="signIn">Sign in</v-btn>
    </v-card-text>
  </v-card>
</template>
