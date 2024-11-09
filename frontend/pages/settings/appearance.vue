<script setup lang="ts">
import {useRouter} from "#app";
import {useUserSettingsStore} from "~/stores/user-settings";

const VEHICLE_COLOR_SETTING_NAME = 'vehicleColor'

const router = useRouter()
const userSettingsStore = useUserSettingsStore()

const vehicleColor = ref<string>('')

watch(vehicleColor, value => {
  const existingValue = userSettingsStore.appearancesMapping[VEHICLE_COLOR_SETTING_NAME]
  if (value && value !== existingValue) {
    userSettingsStore.setAppearance({
      name: VEHICLE_COLOR_SETTING_NAME,
      value
    })
  }
})

onMounted(() => {
  vehicleColor.value = userSettingsStore.appearancesMapping[VEHICLE_COLOR_SETTING_NAME] || ''
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
                  v-model="vehicleColor"
                  swatches-max-height="200px"
                  show-swatches
                  hide-canvas
                  hide-inputs
                  hide-sliders
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
