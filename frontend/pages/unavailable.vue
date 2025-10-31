<script setup lang="ts">
import {useI18n} from "vue-i18n";

const {t} = useI18n()
</script>

<template>
  <NuxtLayout name="unauthenticated">
    <v-card width="100%" max-width="600px">
      <v-card-title>
        Artillery online: {{ t('unavailable.title') }}
      </v-card-title>
      <v-card-text>
        <h3>{{ t('unavailable.message') }}</h3>
      </v-card-text>
    </v-card>
  </NuxtLayout>
</template>
