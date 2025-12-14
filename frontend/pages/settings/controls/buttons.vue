<script setup lang="ts">
import {useRouter} from "#app";
import {useUserSettingsStore} from "~/stores/user-settings";
import {AppearancesNames} from "~/dictionary/appearances-names";
import {useI18n} from "vue-i18n";
import {ControlButtonsAlignments} from "~/dictionary/control-buttons-alignments";

const router = useRouter()
const userSettingsStore = useUserSettingsStore()
const i18n = useI18n()
const {t} = i18n

const settings = reactive({
  showControlButtons: '0',
  controlButtonsAlignment: ControlButtonsAlignments.BOTTOM_HORIZONTAL,
})

const appearances = computed(() => userSettingsStore.appearancesOrDefaultsNameValueMapping)

const controlButtonsAlignments = computed(() => Object.values(ControlButtonsAlignments).map(key =>
    ({key, title: t(`controls.${key}`) })))

watch(() => settings.showControlButtons, value => {
  const existingValue = appearances.value[AppearancesNames.SHOW_CONTROL_BUTTONS]
  if (value && value !== existingValue) {
    userSettingsStore.setAppearance({
      name: AppearancesNames.SHOW_CONTROL_BUTTONS,
      value
    })
  }
})

watch(() => settings.controlButtonsAlignment, value => {
  const existingValue = appearances.value[AppearancesNames.CONTROL_BUTTONS_ALIGNMENT]
  if (value && value !== existingValue) {
    userSettingsStore.setAppearance({
      name: AppearancesNames.CONTROL_BUTTONS_ALIGNMENT,
      value
    })
  }
})

onMounted(() => {
  settings.showControlButtons = appearances.value[AppearancesNames.SHOW_CONTROL_BUTTONS]!
  settings.controlButtonsAlignment = appearances.value[AppearancesNames.CONTROL_BUTTONS_ALIGNMENT]!
})

function back() {
  router.push('/settings/controls')
}
</script>

<template>
  <NuxtLayout>
    <v-card width="100%" max-width="600px">
      <v-card-title>
        Artillery online: {{ t('controls.buttonsTitle') }}
      </v-card-title>
      <v-card-text>
        <v-table class="mb-4" density="compact">
          <tbody>
          <tr>
            <td>{{ t('controls.alwaysShowButtons') }}</td>
            <td>
              <v-switch
                  v-model="settings.showControlButtons"
                  true-value="1"
                  false-value="0"
              />
            </td>
          </tr>
          <tr>
            <td>{{ t('controls.buttonsAlignment') }}</td>
            <td>
              <v-select
                  v-model="settings.controlButtonsAlignment"
                  :items="controlButtonsAlignments"
                  item-value="key"
                  item-title="title"
                  density="compact"
              />
            </td>
          </tr>
          </tbody>
        </v-table>
        <v-btn class="mb-4" width="100%" @click="back">{{ t('common.back') }}</v-btn>
      </v-card-text>
    </v-card>
  </NuxtLayout>
</template>
