<script setup lang="ts">
import {useI18n} from "vue-i18n";
import {useRouter} from "#app";
import {usePresetsStore} from "~/stores/presets";
import type {VehicleSpecs} from "~/playground/data/specs";
import type {UserVehicleConfig} from "~/data/model";
import {ApiRequestSender} from "~/api/api-request-sender";
import {useRequestErrorHandler} from "~/composables/request-error-handler";

const {t} = useI18n()
const router = useRouter()
const presetsStore = usePresetsStore()
const api = new ApiRequestSender()

const selectedVehicle = ref<string>()
const config = ref<UserVehicleConfig>({})

const guns = computed(() => {
  const vehicleSpecs = presetsStore.vehicles[selectedVehicle.value] as VehicleSpecs
  if (!vehicleSpecs) {
    return []
  }
  return Object.keys(vehicleSpecs.availableGuns)
})

const ammo = computed(() => {
  const vehicleSpecs = presetsStore.vehicles[selectedVehicle.value] as VehicleSpecs
  if (!vehicleSpecs) {
    return 0
  }
  return vehicleSpecs.ammo
})

const shells = computed(() => {
  if (!config.value.gun) {
    return []
  }
  const vehicleSpecs = presetsStore.vehicles[selectedVehicle.value] as VehicleSpecs
  if (!vehicleSpecs) {
    return []
  }
  const gunSpecs = vehicleSpecs.availableGuns[config.value.gun]
  if (!gunSpecs) {
    return []
  }
  return Object.keys(gunSpecs.availableShells)
})

watch(selectedVehicle, () => {
  console.log('selectedVehicle')
  config.value = {}
  loadConfig()
})

watch(() => config.value.gun, () => {
  config.value.ammo = {}
  shells.value.forEach(shell => {
    config.value.ammo[shell] = ammo.value / shells.value.length
  })
})

async function loadConfig() {
  try {
    config.value = await api.getJson<UserVehicleConfig>(getUrl())
  } catch (e) {
    useRequestErrorHandler().handle(e)
  }
}

async function saveConfig() {
  try {
    await api.postJson<UserVehicleConfig, undefined>(getUrl(), config.value)
  } catch (e) {
    useRequestErrorHandler().handle(e)
  }
}

function getUrl() {
  return `/user-vehicle-configs/${selectedVehicle.value}`
}

function back() {
  router.push('/settings')
}
</script>

<template>
  <NuxtLayout>
    <v-card width="100%" max-width="600px">
      <v-card-title>
        Artillery online: {{ t('vehicleConfigs.title') }}
      </v-card-title>
      <v-card-text>
        <vehicle-selector @select="v => selectedVehicle = v"/>
        <div v-show="selectedVehicle">
          <v-select
              v-model="config.gun"
              :items="guns"
              density="compact"
              :label="t('vehicleConfigs.selectGun')"
          />
          <div class="mb-4" v-for="shell in shells">
            <div>{{ shell }}</div>
            <v-slider
                v-if="config.ammo && config.ammo[shell]"
                v-model="config.ammo[shell]"
                :max="ammo"
                :min="0"
                class="align-center"
                hide-details
            >
              <template v-slot:append>
                <v-text-field
                    v-model="config.ammo[shell]"
                    density="compact"
                    style="width: 70px"
                    type="number"
                    hide-details
                    single-line
                ></v-text-field>
              </template>
            </v-slider>
          </div>
          <v-btn color="success" class="mb-4" width="100%" @click="saveConfig">{{ t('common.save') }}</v-btn>
        </div>
        <v-btn class="mb-4" width="100%" @click="back">{{ t('common.back') }}</v-btn>
      </v-card-text>
    </v-card>
  </NuxtLayout>
</template>
