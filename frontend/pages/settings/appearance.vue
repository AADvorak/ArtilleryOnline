<script setup lang="ts">
import {useRouter} from "#app";
import {useUserSettingsStore} from "~/stores/user-settings";
import {AppearancesNames} from "~/dictionary/appearances-names";

const router = useRouter()
const userSettingsStore = useUserSettingsStore()

const settings = reactive({
  vehicleColor: '',
  showNicknamesAboveVehicles: '1',
  showHpBarsAboveVehicles: '1',
  showAllPlayersHpBarsInTopBar: '1'
})

const appearances = computed(() => userSettingsStore.appearancesOrDefaultsNameValueMapping)

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

onMounted(() => {
  settings.vehicleColor = appearances.value[AppearancesNames.VEHICLE_COLOR]
  settings.showNicknamesAboveVehicles = appearances.value[AppearancesNames.NICKNAMES_ABOVE]
  settings.showHpBarsAboveVehicles = appearances.value[AppearancesNames.HP_ABOVE]
  settings.showAllPlayersHpBarsInTopBar = appearances.value[AppearancesNames.ALL_HP_TOP]
})

function back() {
  router.push('/settings')
}
</script>

<template>
  <NuxtLayout>
    <v-card width="100%" max-width="600px">
      <v-card-title>
        Artillery online: settings / appearance
      </v-card-title>
      <v-card-text>
        <v-table class="mb-4" density="compact">
          <tbody>
          <tr>
            <td>Vehicle color</td>
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
            <td>Show nicknames above vehicles</td>
            <td>
              <v-switch
                  v-model="settings.showNicknamesAboveVehicles"
                  true-value="1"
                  false-value="0"
              />
            </td>
          </tr>
          <tr>
            <td>Show HP bars above vehicles</td>
            <td>
              <v-switch
                  v-model="settings.showHpBarsAboveVehicles"
                  true-value="1"
                  false-value="0"
              />
            </td>
          </tr>
          <tr>
            <td>Show all players HP bars in top bar</td>
            <td>
              <v-switch
                  v-model="settings.showAllPlayersHpBarsInTopBar"
                  true-value="1"
                  false-value="0"
              />
            </td>
          </tr>
          </tbody>
        </v-table>
        <v-btn class="mb-4" width="100%" @click="back">Back</v-btn>
      </v-card-text>
    </v-card>
  </NuxtLayout>
</template>
