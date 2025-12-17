<script setup lang="ts">
import {useRouter} from "#app";
import {useUserSettingsStore} from "~/stores/user-settings";
import {AppearancesNames} from "~/dictionary/appearances-names";
import {useI18n} from "vue-i18n";
import {ControlButtonsAlignments} from "~/dictionary/control-buttons-alignments";
import {ControlsTypes} from "~/dictionary/controls-types";

const router = useRouter()
const userSettingsStore = useUserSettingsStore()
const i18n = useI18n()
const {t} = i18n

const settings = reactive({
  showControlButtons: '0',
  controlButtonsAlignment: ControlButtonsAlignments.BOTTOM,
  screenControlsType: ControlsTypes.JOYSTICKS
})

const appearances = computed(() => userSettingsStore.appearancesOrDefaultsNameValueMapping)

const controlButtonsAlignments = computed(() => Object.values(ControlButtonsAlignments).map(key =>
    ({key, title: t(`controls.${key}`) })))

const screenControlsTypes = computed(() => Object.values(ControlsTypes).map(key =>
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

watch(() => settings.screenControlsType, value => {
  const existingValue = appearances.value[AppearancesNames.CONTROLS_TYPE]
  if (value && value !== existingValue) {
    userSettingsStore.setAppearance({
      name: AppearancesNames.CONTROLS_TYPE,
      value
    })
  }
})

onMounted(() => {
  settings.showControlButtons = appearances.value[AppearancesNames.SHOW_CONTROL_BUTTONS]!
  settings.controlButtonsAlignment = appearances.value[AppearancesNames.CONTROL_BUTTONS_ALIGNMENT]!
  settings.screenControlsType = appearances.value[AppearancesNames.CONTROLS_TYPE]!
})

function back() {
  router.push('/settings/controls')
}
</script>

<template>
  <NuxtLayout>
    <v-card width="100%" max-width="600px">
      <v-card-title>
        Artillery online: {{ t('controls.screenControlsTitle') }}
      </v-card-title>
      <v-card-text>
        <v-table class="mb-4" density="compact">
          <tbody>
          <tr>
            <td>{{ t('controls.alwaysShowScreenControls') }}</td>
            <td>
              <v-switch
                  v-model="settings.showControlButtons"
                  true-value="1"
                  false-value="0"
              />
            </td>
          </tr>
          <tr>
            <td>{{ t('controls.screenControlsAlignment') }}</td>
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
          <tr>
            <td>{{ t('controls.screenControlsType') }}</td>
            <td>
              <v-select
                  v-model="settings.screenControlsType"
                  :items="screenControlsTypes"
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
