<script setup lang="ts">
import {useRouter} from "#app";
import {useI18n} from "vue-i18n";
import VersionInfo from "~/components/version-info.vue";

const {t} = useI18n()
const router = useRouter()

function logIn() {
  router.push('/login')
}

function signUp() {
  router.push('/signup')
}
</script>

<template>
  <NuxtLayout name="unauthenticated">
    <v-card width="100%" max-width="600px">
      <v-card-title>
        Artillery online
      </v-card-title>
      <v-card-text>
        <v-btn class="mb-4" width="100%" color="primary" @click="logIn">{{ t('common.logIn') }}</v-btn>
        <v-btn class="mb-4" width="100%" color="secondary" @click="signUp">{{ t('common.signUp') }}</v-btn>
        <version-info/>
      </v-card-text>
    </v-card>
  </NuxtLayout>
</template>
