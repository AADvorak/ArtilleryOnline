<script setup lang="ts">
import {ref} from "vue";
import {useErrorsStore} from "~/stores/errors";

const errorsStore = useErrorsStore()

const opened = ref(false)
const message = ref('')

watch(() => errorsStore.errors, () => {
  if (errorsStore.errors.length) {
    const error = errorsStore.errors.shift()
    if (error.status === 404 && !error.error) {
      message.value = 'Requested resource is not found'
    } else if (error.error) {
      message.value = error.error.message
    }
    if (message.value) {
      opened.value = true
    }
  }
}, {deep: true})

function hideAndClear() {
  opened.value = false
  message.value = ''
}
</script>

<template>
  <v-dialog :model-value="opened" :persistent="true" max-width="600px">
    <v-card width="100%">
      <v-card-title>Error</v-card-title>
      <v-card-text>
        <div class="d-flex">{{ message }}</div>
        <div class="d-flex mt-4">
          <v-btn color="primary" @click="hideAndClear">OK</v-btn>
        </div>
      </v-card-text>
    </v-card>
  </v-dialog>
</template>
