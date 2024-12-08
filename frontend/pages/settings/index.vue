<script setup lang="ts">
import {useRouter} from "#app";
import {useI18n} from "vue-i18n";

const {t} = useI18n()
const router = useRouter()

function toControls() {
  router.push('/settings/controls')
}

function toAppearance() {
  router.push('/settings/appearance')
}

function toSounds() {
  router.push('/settings/sounds')
}

function back() {
  router.push('/menu')
}
</script>

<template>
  <NuxtLayout>
    <v-card width="100%" max-width="600px">
      <v-card-title>
        Artillery online: {{ t('settings.title') }}
      </v-card-title>
      <v-card-text>
        <v-btn class="mb-4" width="100%" color="secondary" @click="toControls">{{ t('settings.controls') }}</v-btn>
        <v-btn class="mb-4" width="100%" color="secondary" @click="toAppearance">{{ t('settings.appearance') }}</v-btn>
        <v-btn class="mb-4" width="100%" color="secondary" @click="toSounds">{{ t('settings.sounds') }}</v-btn>
        <v-btn class="mb-4" width="100%" @click="back">{{ t('common.back') }}</v-btn>
      </v-card-text>
    </v-card>
  </NuxtLayout>
</template>
