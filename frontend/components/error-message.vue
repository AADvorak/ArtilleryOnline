<script setup lang="ts">
import {ref} from "vue";
import {useErrorsStore} from "~/stores/errors";
import {useI18n} from "vue-i18n";
import type {ApiErrorResponse} from "~/data/response";

const {t} = useI18n()
const errorsStore = useErrorsStore()

const opened = ref(false)
const message = ref('')

watch(() => errorsStore.errors, () => {
  if (errorsStore.errors.length) {
    const error = errorsStore.errors.shift()
    if (error.status === 404 && !error.error) {
      message.value = t('common.notFound')
    } else if (error.error) {
      message.value = getErrorMessage(error.error)
    }
    if (message.value) {
      opened.value = true
    }
  }
}, {deep: true})

function getErrorMessage(error: ApiErrorResponse) {
  if (error.locale) {
    return t('serverMessages.' + error.locale.code, error.locale.params)
  } else {
    return error.message
  }
}

function hideAndClear() {
  opened.value = false
  message.value = ''
}
</script>

<template>
  <v-dialog :model-value="opened" :persistent="true" max-width="600px">
    <v-card width="100%">
      <v-card-title>{{ t('common.error') }}</v-card-title>
      <v-card-text>
        <div class="d-flex">{{ message }}</div>
        <div class="d-flex mt-4">
          <v-btn color="primary" @click="hideAndClear">{{ t('common.ok') }}</v-btn>
        </div>
      </v-card-text>
    </v-card>
  </v-dialog>
</template>
