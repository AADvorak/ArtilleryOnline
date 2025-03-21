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
const submitting = ref<boolean>(false)
const savedConfigJson = ref<string>('')

const vehicleSpecs = computed<VehicleSpecs | undefined>(() => {
  if (!selectedVehicle.value) {
    return undefined
  }
  return presetsStore.vehicles[selectedVehicle.value] as VehicleSpecs
})

const guns = computed(() => {
  if (!vehicleSpecs.value) {
    return []
  }
  return Object.keys(vehicleSpecs.value.availableGuns)
})

const maxAmmo = computed(() => {
  if (!vehicleSpecs.value) {
    return 0
  }
  return vehicleSpecs.value.ammo
})

const shells = computed(() => {
  if (!config.value.gun) {
    return []
  }
  if (!vehicleSpecs.value) {
    return []
  }
  const gunSpecs = vehicleSpecs.value.availableGuns[config.value.gun]
  if (!gunSpecs) {
    return []
  }
  return Object.keys(gunSpecs.availableShells)
})

const noChanges = computed(() => {
  if (!savedConfigJson.value) {
    return true
  }
  return JSON.stringify(config.value) === savedConfigJson.value
})

watch(selectedVehicle, () => {
  config.value = {}
  loadConfig()
})

watch(() => config.value.gun, () => {
  if (!config.value.ammo) {
    config.value.ammo = {}
  }
  if (!Object.keys(config.value.ammo).length) {
    shells.value.forEach(shell => {
      config.value.ammo[shell] = maxAmmo.value / shells.value.length
    })
  }
})

watch(() => config.value.ammo, () => {
  const ammo = config.value.ammo
  if (!ammo) {
    return
  }
  const keys = Object.keys(ammo)
  for (const key of keys) {
    ammo[key] = parseInt(ammo[key])
  }
  const sumAmmo = Object.values(ammo).reduce((a, b) => a + b, 0)
  if (sumAmmo > maxAmmo.value) {
    let max = keys[0]
    for (const key of keys) {
      if (ammo[key] > ammo[max]) {
        max = key
      }
    }
    ammo[max] -= sumAmmo - maxAmmo.value
  }
}, {deep: true})

async function loadConfig() {
  try {
    config.value = await api.getJson<UserVehicleConfig>(getUrl())
    if (!config.value.gun) {
      config.value.gun = vehicleSpecs.value?.defaultGun
    }
    savedConfigJson.value = JSON.stringify(config.value)
  } catch (e) {
    useRequestErrorHandler().handle(e)
  }
}

async function saveConfig() {
  try {
    submitting.value = true
    await api.postJson<UserVehicleConfig, undefined>(getUrl(), config.value)
    savedConfigJson.value = JSON.stringify(config.value)
  } catch (e) {
    useRequestErrorHandler().handle(e)
  } finally {
    submitting.value = false
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
            <template v-if="config.ammo && config.ammo[shell] !== undefined">
              <div>{{ shell }}</div>
              <v-slider
                  v-model="config.ammo[shell]"
                  :max="maxAmmo"
                  :min="0"
                  class="align-center"
                  hide-details
              >
                <template v-slot:append>
                  <v-text-field
                      v-model="config.ammo[shell]"
                      density="compact"
                      style="width: 90px"
                      type="number"
                      hide-details
                      single-line
                  ></v-text-field>
                </template>
              </v-slider>
            </template>
          </div>
          <v-btn color="success" class="mb-4" width="100%"
                 :loading="submitting" :disabled="noChanges" @click="saveConfig">
            {{ t('common.save') }}
          </v-btn>
        </div>
        <v-btn class="mb-4" width="100%" @click="back">{{ t('common.back') }}</v-btn>
      </v-card-text>
    </v-card>
  </NuxtLayout>
</template>
