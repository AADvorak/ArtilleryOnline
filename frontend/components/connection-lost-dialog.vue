<script setup lang="ts">
import {ref} from "vue";
import {useStompClientStore} from "~/stores/stomp-client";
import {useUserStore} from "~/stores/user";
import {mdiReload} from "@mdi/js";
import {useI18n} from "vue-i18n";

const {t} = useI18n()
const stompClientStore = useStompClientStore()
const userStore = useUserStore()

const opened = ref(false)

watch(() => stompClientStore.connected, value => {
  if (!value && userStore.user) {
    opened.value = true
  }
})

function hideAndReload() {
  opened.value = false
  location.reload()
}
</script>

<template>
  <v-dialog :model-value="opened" :persistent="true" max-width="600px">
    <v-card width="100%">
      <v-card-title>{{ t('common.error') }}</v-card-title>
      <v-card-text>
        <div class="d-flex">{{ t('connectionLostDialog.message') }}</div>
        <div class="d-flex mt-4">
          <v-btn
              :prepend-icon="mdiReload"
              color="primary"
              @click="hideAndReload">
            {{ t('connectionLostDialog.reload') }}
          </v-btn>
        </div>
      </v-card-text>
    </v-card>
  </v-dialog>
</template>
