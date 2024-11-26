<script setup lang="ts">
import {useRouter} from "#app";

const router = useRouter()

function toProfile() {
  router.push('/user/profile')
}

function toBattleHistory() {
  router.push('/user/battle-history')
}

function toBattleStatistics() {
  router.push('/user/battle-statistics')
}

function back() {
  router.push('/menu')
}
</script>

<template>
  <NuxtLayout>
    <v-card width="100%" max-width="600px">
      <v-card-title>
        Artillery online: user
      </v-card-title>
      <v-card-text>
        <v-btn class="mb-4" width="100%" color="secondary" @click="toProfile">Profile</v-btn>
        <v-btn class="mb-4" width="100%" color="secondary" @click="toBattleHistory">Battle history</v-btn>
        <v-btn class="mb-4" width="100%" color="secondary" @click="toBattleStatistics">Battle statistics</v-btn>
        <v-btn class="mb-4" width="100%" @click="back">Back</v-btn>
      </v-card-text>
    </v-card>
  </NuxtLayout>
</template>
