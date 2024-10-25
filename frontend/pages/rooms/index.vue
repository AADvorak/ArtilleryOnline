<script setup lang="ts">
import {useRouter} from "#app";

const router = useRouter()

function toRoom() {
  router.push('/rooms/room')
}

function back() {
  router.push('/menu')
}
</script>

<template>
  <v-card width="100%" max-width="600px">
    <v-card-title>
      Artillery online: rooms
    </v-card-title>
    <v-card-text>
      <v-btn class="mb-4" width="100%" color="secondary" @click="toRoom">Create room</v-btn>
      <v-btn class="mb-4" width="100%" @click="back">Back</v-btn>
    </v-card-text>
  </v-card>
</template>
