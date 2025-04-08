<script setup lang="ts">
import {ref} from 'vue'
import {useUserSettingsStore} from '~/stores/user-settings'
import {useI18n} from "vue-i18n";

const {t} = useI18n()

const userSettingsStore = useUserSettingsStore()
const controls = userSettingsStore.controlsOrDefaults

const opened = ref(false)

function removeKeyStr(str: string) {
  return str.replace('Key', '')
}

function show() {
  opened.value = true
}

function hide() {
  opened.value = false
}

defineExpose({
  show
})
</script>

<template>
  <v-dialog :model-value="opened" :persistent="true" max-width="600px">
    <v-card width="100%">
      <v-card-title>{{ t('common.help') }}</v-card-title>
      <v-card-text>
        <v-table density="compact">
          <tbody>
          <tr v-for="control of controls">
            <td>{{ t('controls.' + control.name) }}</td>
            <td>{{ removeKeyStr(control.value) }}</td>
          </tr>
          </tbody>
        </v-table>
        <div class="d-flex mt-4">
          <v-btn color="primary" @click="hide">{{ t('common.ok') }}</v-btn>
        </div>
      </v-card-text>
    </v-card>
  </v-dialog>
</template>
