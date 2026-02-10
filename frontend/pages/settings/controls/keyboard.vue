<script setup lang="ts">
import {useRouter} from "#app";
import {useUserSettingsStore} from "~/stores/user-settings";
import ControlEditor from "~/components/control-editor.vue";
import type {UserSetting} from "~/data/model";
import {useI18n} from "vue-i18n";

const RESERVED_KEY_CODES = [
  'Escape', 'F1', 'F2', 'F3', 'F4', 'F5', 'F6', 'F7', 'F8', 'F9', 'F10', 'F11', 'F12',
  'Digit1', 'Digit2', 'Digit3', 'Digit4', 'Digit5', 'Digit6', 'Digit7', 'Digit8', 'Digit9', 'Digit0'
]

const {t} = useI18n()
const router = useRouter()
const userSettingsStore = useUserSettingsStore()

const editing = ref<boolean>(false)

const controls = computed(() => userSettingsStore.controlsOrDefaults)

function onEditStart() {
  editing.value = true
}

function onEditEnd(newControl: UserSetting) {
  editing.value = false
  if (RESERVED_KEY_CODES.includes(newControl.value)) {
    return
  }
  for (const control of controls.value) {
    if (control.name !== newControl.name && control.value === newControl.value) {
      return
    }
  }
  userSettingsStore.setControl(newControl)
}

function back() {
  router.push('/settings/controls')
}
</script>

<template>
  <NuxtLayout>
    <v-card width="100%" max-width="600px">
      <v-card-title>
        <menu-navigation/>
      </v-card-title>
      <v-card-text>
        <v-table class="mb-4" density="compact">
          <tbody>
          <tr v-for="control of controls">
            <td>{{ t('controls.' + control.name) }}</td>
            <td style="text-align: right">
              <control-editor
                  :control="control"
                  @edit-start="onEditStart"
                  @edit-end="onEditEnd"/>
            </td>
          </tr>
          </tbody>
        </v-table>
        <v-btn class="mb-4" color="secondary" width="100%"
               @click="userSettingsStore.resetControls">{{ t('controls.resetToDefaults') }}</v-btn>
        <v-btn class="mb-4" width="100%" @click="back">{{ t('common.back') }}</v-btn>
      </v-card-text>
    </v-card>
  </NuxtLayout>
</template>
