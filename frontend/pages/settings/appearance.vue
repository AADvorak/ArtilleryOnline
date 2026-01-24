<script setup lang="ts">
import {useRouter} from "#app";
import {useUserSettingsStore} from "~/stores/user-settings";
import {AppearancesNames} from "~/dictionary/appearances-names";
import {useI18n} from "vue-i18n";
import {BattlefieldAlignments} from "~/dictionary/battlefield-alignments";

const router = useRouter()
const userSettingsStore = useUserSettingsStore()
const i18n = useI18n()
const {t} = i18n

const settings = reactive({
  language: 'en',
  vehicleColor: '',
  showNicknamesAboveVehicles: '1',
  showHpBarsAboveVehicles: '1',
  showAllPlayersHpBarsInTopBar: '0',
  showGroundTextureAndBackground: '0',
  battlefieldAlignment: BattlefieldAlignments.BY_SCREEN_SIZE
})

const appearances = computed(() => userSettingsStore.appearancesOrDefaultsNameValueMapping)
const battlefieldAlignments = computed(() => Object.values(BattlefieldAlignments).map(key =>
    ({key, title: t(`appearance.${key}`) })))

watch(() => settings.language, value => {
  const existingValue = appearances.value[AppearancesNames.LANGUAGE]
  if (value && value !== existingValue) {
    userSettingsStore.setAppearance({
      name: AppearancesNames.LANGUAGE,
      value
    })
  }
})

watch(() => settings.vehicleColor, value => {
  const existingValue = appearances.value[AppearancesNames.VEHICLE_COLOR]
  if (value && value !== existingValue) {
    userSettingsStore.setAppearance({
      name: AppearancesNames.VEHICLE_COLOR,
      value
    })
  }
})

watch(() => settings.showNicknamesAboveVehicles, value => {
  const existingValue = appearances.value[AppearancesNames.NICKNAMES_ABOVE]
  if (value && value !== existingValue) {
    userSettingsStore.setAppearance({
      name: AppearancesNames.NICKNAMES_ABOVE,
      value
    })
  }
})

watch(() => settings.showHpBarsAboveVehicles, value => {
  const existingValue = appearances.value[AppearancesNames.HP_ABOVE]
  if (value && value !== existingValue) {
    userSettingsStore.setAppearance({
      name: AppearancesNames.HP_ABOVE,
      value
    })
  }
})

watch(() => settings.showAllPlayersHpBarsInTopBar, value => {
  const existingValue = appearances.value[AppearancesNames.ALL_HP_TOP]
  if (value && value !== existingValue) {
    userSettingsStore.setAppearance({
      name: AppearancesNames.ALL_HP_TOP,
      value
    })
  }
})

watch(() => settings.showGroundTextureAndBackground, value => {
  const existingValue = appearances.value[AppearancesNames.GROUND_TEXTURE_BACKGROUND]
  if (value && value !== existingValue) {
    userSettingsStore.setAppearance({
      name: AppearancesNames.GROUND_TEXTURE_BACKGROUND,
      value
    })
  }
})

watch(() => settings.battlefieldAlignment, value => {
  const existingValue = appearances.value[AppearancesNames.BATTLEFIELD_ALIGNMENT]
  if (value && value !== existingValue) {
    userSettingsStore.setAppearance({
      name: AppearancesNames.BATTLEFIELD_ALIGNMENT,
      value
    })
  }
})

onMounted(() => {
  settings.language = appearances.value[AppearancesNames.LANGUAGE]!
  settings.vehicleColor = appearances.value[AppearancesNames.VEHICLE_COLOR]!
  settings.showNicknamesAboveVehicles = appearances.value[AppearancesNames.NICKNAMES_ABOVE]!
  settings.showHpBarsAboveVehicles = appearances.value[AppearancesNames.HP_ABOVE]!
  settings.showAllPlayersHpBarsInTopBar = appearances.value[AppearancesNames.ALL_HP_TOP]!
  settings.showGroundTextureAndBackground = appearances.value[AppearancesNames.GROUND_TEXTURE_BACKGROUND]!
  settings.battlefieldAlignment = appearances.value[AppearancesNames.BATTLEFIELD_ALIGNMENT]!
})

function back() {
  router.push('/settings')
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
          <tr>
            <td>{{ t('appearance.language') }}</td>
            <td>
              <v-select
                  v-model="settings.language"
                  :items="i18n.availableLocales"
                  density="compact"
              />
            </td>
          </tr>
          <tr>
            <td>{{ t('appearance.vehicleColor') }}</td>
            <td>
              <v-color-picker
                  v-model="settings.vehicleColor"
                  swatches-max-height="200px"
                  show-swatches
                  hide-canvas
                  hide-inputs
                  hide-sliders
              />
            </td>
          </tr>
          <tr>
            <td>{{ t('appearance.showNicknamesAboveVehicles') }}</td>
            <td>
              <v-switch
                  v-model="settings.showNicknamesAboveVehicles"
                  true-value="1"
                  false-value="0"
              />
            </td>
          </tr>
          <tr>
            <td>{{ t('appearance.showHpBarsAboveVehicles') }}</td>
            <td>
              <v-switch
                  v-model="settings.showHpBarsAboveVehicles"
                  true-value="1"
                  false-value="0"
              />
            </td>
          </tr>
          <tr>
            <td>{{ t('appearance.showAllPlayersHpBarsInTopBar') }}</td>
            <td>
              <v-switch
                  v-model="settings.showAllPlayersHpBarsInTopBar"
                  true-value="1"
                  false-value="0"
              />
            </td>
          </tr>
          <tr>
            <td>{{ t('appearance.showGroundTextureAndBackground') }}</td>
            <td>
              <v-switch
                  v-model="settings.showGroundTextureAndBackground"
                  true-value="1"
                  false-value="0"
              />
            </td>
          </tr>
          <tr>
            <td>{{ t('appearance.battlefieldAlignment') }}</td>
            <td>
              <v-select
                  v-model="settings.battlefieldAlignment"
                  :items="battlefieldAlignments"
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
