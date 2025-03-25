<script setup lang="ts">
import {useI18n} from "vue-i18n";
import {useRouter} from "#app";
import {usePresetsStore} from "~/stores/presets";
import type {VehicleSpecs} from "~/playground/data/specs";
import type {UserVehicleConfig} from "~/data/model";
import {ApiRequestSender} from "~/api/api-request-sender";
import {useRequestErrorHandler} from "~/composables/request-error-handler";
import GunSpecsDialog from "~/components/gun-specs-dialog.vue";
import {mdiInformationOutline} from "@mdi/js";
import IconBtn from "~/components/icon-btn.vue";
import ShellSpecsDialog from "~/components/shell-specs-dialog.vue";
import type {Ammo} from "~/playground/data/common";

const {t} = useI18n()
const router = useRouter()
const presetsStore = usePresetsStore()
const api = new ApiRequestSender()

const selectedVehicle = ref<string>()
const config = ref<UserVehicleConfig>({})
const submitting = ref<boolean>(false)
const savedConfigJson = ref<string>('')
const oldAmmo = ref<Ammo>({})

const gunSpecsDialog = ref<InstanceType<typeof GunSpecsDialog> | null>(null)
const shellSpecsDialog = ref<InstanceType<typeof ShellSpecsDialog> | null>(null)

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
  if (!gunSpecs.value) {
    return 0
  }
  return gunSpecs.value.ammo
})

const gunSpecs = computed(() => {
  if (!config.value.gun) {
    return undefined
  }
  if (!vehicleSpecs.value) {
    return undefined
  }
  return vehicleSpecs.value.availableGuns[config.value.gun]
})

const shells = computed(() => {
  if (!gunSpecs.value) {
    return []
  }
  return Object.keys(gunSpecs.value.availableShells)
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

watch(() => config.value.gun, (value, oldValue) => {
  if (!value || value && oldValue || !config.value.ammo) {
    config.value.ammo = {}
  }
  if (!Object.keys(config.value.ammo).length) {
    // todo set default number of shells
    const maxSgnLShells = 10
    shells.value.forEach(shell => {
      if (shell === 'SGN-L') {
        config.value.ammo[shell] = maxSgnLShells
      } else if (['AP-L', 'HE-L'].includes(shell)) {
        config.value.ammo[shell] = maxAmmo.value - maxSgnLShells
      } else {
        config.value.ammo[shell] = maxAmmo.value / shells.value.length
      }
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
    let changed = ''
    for (const key of keys) {
      if (oldAmmo.value[key] !== ammo[key]) {
        changed = key
      }
    }
    let max = ''
    for (const key of keys) {
      if (key !== changed && (!max || ammo[key] > ammo[max])) {
        max = key
      }
    }
    ammo[max] -= sumAmmo - maxAmmo.value
  }
  oldAmmo.value = JSON.parse(JSON.stringify(ammo))
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

function showGunSpecsDialog() {
  gunSpecsDialog.value?.show()
}

function showShellSpecsDialog(shellName: string) {
  if (!gunSpecs.value) {
    return
  }
  shellSpecsDialog.value?.show(shellName, gunSpecs.value.availableShells[shellName], gunSpecs.value)
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
          >
            <template v-if="!!config.gun" v-slot:append>
              <icon-btn
                  :icon="mdiInformationOutline"
                  :tooltip="t('common.specs')"
                  @click="showGunSpecsDialog"
              />
            </template>
          </v-select>
          <div class="mb-4" v-for="shell in shells">
            <template v-if="config.ammo && config.ammo[shell] !== undefined">
              <div>
                {{ shell }}
                <icon-btn
                    :icon="mdiInformationOutline"
                    :tooltip="t('common.specs')"
                    @click="showShellSpecsDialog(shell)"
                />
              </div>
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
    <gun-specs-dialog v-if="!!config.gun" ref="gunSpecsDialog" :gun-name="config.gun" :gun-specs="gunSpecs"/>
    <shell-specs-dialog v-if="!!config.gun" ref="shellSpecsDialog"/>
  </NuxtLayout>
</template>
