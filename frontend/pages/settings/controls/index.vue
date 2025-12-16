<script setup lang="ts">
import {useI18n} from "vue-i18n";
import {useRouter} from "#app";

const {t} = useI18n()
const router = useRouter()

function toKeyboard() {
  router.push('/settings/controls/keyboard')
}

function toScreenControls() {
  router.push('/settings/controls/screen')
}

function back() {
  router.push('/settings')
}
</script>

<template>
  <NuxtLayout>
    <v-card width="100%" max-width="600px">
      <v-card-title>
        Artillery online: {{ t('controls.title') }}
      </v-card-title>
      <v-card-text>
        <v-btn class="mb-4" width="100%" color="secondary" @click="toKeyboard">{{ t('controls.keyboard') }}</v-btn>
        <v-btn class="mb-4" width="100%" color="secondary" @click="toScreenControls">{{ t('controls.screenControls') }}</v-btn>
        <v-btn class="mb-4" width="100%" @click="back">{{ t('common.back') }}</v-btn>
      </v-card-text>
    </v-card>
  </NuxtLayout>
</template>
