<script setup lang="ts">
import {useRouter} from "#app";
import {useI18n} from "vue-i18n";

const {t} = useI18n()
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
        <menu-navigation/>
      </v-card-title>
      <v-card-text>
        <v-btn class="mb-4" width="100%" color="secondary" @click="toProfile">{{ t('user.profile') }}</v-btn>
        <v-btn class="mb-4" width="100%" color="secondary" @click="toBattleHistory">{{ t('user.battleHistory') }}</v-btn>
        <v-btn class="mb-4" width="100%" color="secondary" @click="toBattleStatistics">{{ t('user.battleStatistics') }}</v-btn>
        <v-btn class="mb-4" width="100%" @click="back">{{ t('common.back') }}</v-btn>
      </v-card-text>
    </v-card>
  </NuxtLayout>
</template>
