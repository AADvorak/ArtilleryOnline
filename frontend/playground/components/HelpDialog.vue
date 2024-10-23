<script setup lang="ts">
import {ref} from 'vue'
import {useUserSettingsStore} from '~/stores/user-settings'

const userSettingsStore = useUserSettingsStore()
const controls = userSettingsStore.controlsOrDefaults

const opened = ref(false)

onMounted(() => {
  addEventListener('keyup', showHelpIfF1Pressed)
})

onBeforeUnmount(() => {
  removeEventListener('keyup', showHelpIfF1Pressed)
})

function showHelpIfF1Pressed(e) {
  if (e.code === 'F1') {
    opened.value = true
  }
}

function removeKeyStr(str: string) {
  return str.replace('Key', '')
}

function hide() {
  opened.value = false
}
</script>

<template>
  <v-dialog :model-value="opened" :persistent="true" max-width="600px">
    <v-card width="100%">
      <v-card-title>Help</v-card-title>
      <v-card-text>
        <v-table density="compact">
          <tbody>
          <tr v-for="control of controls">
            <td>{{ control.description }}</td>
            <td>{{ removeKeyStr(control.value) }}</td>
          </tr>
          </tbody>
        </v-table>
        <div class="d-flex mt-4">
          <v-btn color="primary" @click="hide">OK</v-btn>
        </div>
      </v-card-text>
    </v-card>
  </v-dialog>
</template>
