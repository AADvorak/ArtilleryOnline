<script setup lang="ts">
import {useRouter} from "#app";

const router = useRouter()

function randomBattle() {

}

function back() {
  router.push('/menu')
}
</script>

<template>
  <v-card width="100%" max-width="600px">
    <v-card-title>
      Artillery online: battle
    </v-card-title>
    <v-card-text>
      <v-btn class="mb-4" width="100%" color="error" @click="randomBattle">Random battle</v-btn>
      <v-btn class="mb-4" width="100%" @click="back">Back</v-btn>
    </v-card-text>
  </v-card>
</template>
