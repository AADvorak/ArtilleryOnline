<script setup lang="ts">
import {useI18n} from "vue-i18n";
import {useRouter, useRoute} from "#app";
import {usePresetsStore} from "~/stores/presets";
import type {VehicleSpecs} from "~/playground/data/specs";
import type {UserVehicleConfig} from "~/data/model";
import {useRequestErrorHandler} from "~/composables/request-error-handler";
import GunSpecsDialog from "~/components/gun-specs-dialog.vue";
import {mdiInformationOutline} from "@mdi/js";
import IconBtn from "~/components/icon-btn.vue";
import ShellSpecsDialog from "~/components/shell-specs-dialog.vue";
import type {Ammo} from "~/playground/data/common";
import {useConfigsStore} from "~/stores/configs";
import VehicleSelector from "~/components/vehicle-selector.vue";
import Draggable from "vuedraggable";
import type {AmmoConfig} from "~/playground/data/config";
import type {ErrorResponse} from "~/data/response";

const SGN_L_SHELL = 'SGN-L'

const {t} = useI18n()
const router = useRouter()
const route = useRoute()
const presetsStore = usePresetsStore()
const configsStore = useConfigsStore()

const selectedVehicle = ref<string>()
const config = ref<UserVehicleConfig>({})
const submitting = ref<boolean>(false)
const savedConfigJson = ref<string>('')
const oldAmmo = ref<AmmoConfig[]>([])

const gunSpecsDialog = ref<InstanceType<typeof GunSpecsDialog> | null>(null)
const shellSpecsDialog = ref<InstanceType<typeof ShellSpecsDialog> | null>(null)
const vehicleSelector = ref<InstanceType<typeof VehicleSelector> | undefined>()

const vehicleSpecs = computed<VehicleSpecs | undefined>(() => {
  if (!selectedVehicle.value || !presetsStore.vehicles) {
    return undefined
  }
  return presetsStore.vehicles[selectedVehicle.value] as VehicleSpecs
})

const guns = computed(() => {
  if (!vehicleSpecs.value) {
    return []
  }
  return Object.keys(vehicleSpecs.value.availableGuns)
      .map(key => ({key, title: getGunTitle(key)}))
})

const maxAmmo = computed(() => {
  if (!gunSpecs.value) {
    return 0
  }
  return gunSpecs.value.ammo
})

const maxSgnShells = computed(() => {
  return bomberFlightsNumber.value ? bomberFlightsNumber.value * 2 : 20
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

const availableShellsNames = computed(() => {
  if (!gunSpecs.value) {
    return []
  }
  return Object.keys(gunSpecs.value.availableShells).sort()
})

const noChanges = computed(() => {
  if (!savedConfigJson.value) {
    return true
  }
  return JSON.stringify(config.value) === savedConfigJson.value
})

const shellsOrderNumbers = computed(() => {
  const orderNumbers: Ammo = {}
  const ammo = config.value.ammo
  if (!ammo) {
    return orderNumbers
  }
  for (let number = 0; number < ammo.length; number++) {
    orderNumbers[ammo[number]!.name] = number + 1
  }
  return orderNumbers
})

const warnings = computed(() => {
  const warnings: string[] = []
  const ammo = config.value.ammo
  if (ammo) {
    const sumAmmo = ammo.map(item => parseInt(String(item.amount))).reduce((a, b) => a + b, 0)
    if (sumAmmo < maxAmmo.value) {
      warnings.push('incompleteAmmo')
    }
    const sgnShellsAmount = ammo.filter(item => item.name === SGN_L_SHELL)[0]?.amount
    if (bomberFlightsNumber.value && (!sgnShellsAmount || sgnShellsAmount < bomberFlightsNumber.value)) {
      warnings.push('lowSignalShells')
    }
  }
  return warnings
})

const bomberFlightsNumber = computed(() => {
  return Object.values(vehicleSpecs.value?.availableBombers || {})[0]?.flights
})

watch(vehicleSpecs, value => {
  config.value = {}
  value && loadConfig()
})

watch(selectedVehicle, (value) => {
  if (value) {
    router.replace({
      query: { ...route.query, selectedVehicle: value }
    })
  } else {
    const { selectedVehicle, ...newQuery } = route.query
    router.replace({ query: newQuery })
  }
})

watch(() => config.value.gun, (value, oldValue) => {
  if (!value || value && oldValue || !config.value.ammo) {
    const defaultSgnShells = Math.floor((bomberFlightsNumber.value || 6) * 1.5)
    config.value.ammo = availableShellsNames.value.map(name => {
      let amount
      if (name === SGN_L_SHELL) {
        amount = defaultSgnShells
      } else if (['AP-L', 'HE-L'].includes(name)) {
        amount = maxAmmo.value - defaultSgnShells
      } else {
        amount = maxAmmo.value / availableShellsNames.value.length
      }
      return {name, amount}
    })
  }
})

watch(() => config.value.ammo, () => {
  const ammo = config.value.ammo
  if (!ammo) {
    return
  }
  const ammoMap: Ammo = {}
  ammo.forEach(item => ammoMap[item.name] = getRestrictedAmount(item))
  const oldAmmoMap: Ammo = {}
  oldAmmo.value.forEach(item => oldAmmoMap[item.name] = item.amount)
  const sumAmmo = Object.values(ammoMap).reduce((a, b) => a + b, 0)
  const keys = Object.keys(ammoMap)
  if (sumAmmo > maxAmmo.value) {
    let changed = ''
    for (const key of keys) {
      if (oldAmmoMap[key] !== ammoMap[key]) {
        changed = key
      }
    }
    let max = ''
    for (const key of keys) {
      if (key !== changed && (!max || ammoMap[key]! > ammoMap[max]!)) {
        max = key
      }
    }
    ammoMap[max]! -= sumAmmo - maxAmmo.value
  }
  ammo.forEach(item => item.amount = ammoMap[item.name]!)
  oldAmmo.value = JSON.parse(JSON.stringify(ammo))
}, {deep: true})

onMounted(() => {
  const selectedVehicleParam = route.query.selectedVehicle as string
  if (selectedVehicleParam) {
    selectedVehicle.value = selectedVehicleParam
    vehicleSelector.value?.setSelectedVehicle(selectedVehicleParam)
  }
})

async function loadConfig() {
  try {
    config.value = await configsStore.loadVehicleConfig(vehicleSpecs.value!)
    savedConfigJson.value = JSON.stringify(config.value)
  } catch (e) {
    useRequestErrorHandler().handle(e as ErrorResponse)
  }
}

async function saveConfig() {
  try {
    submitting.value = true
    await configsStore.saveVehicleConfig(selectedVehicle.value!, config.value)
    savedConfigJson.value = JSON.stringify(config.value)
  } catch (e) {
    useRequestErrorHandler().handle(e as ErrorResponse)
  } finally {
    submitting.value = false
  }
}

function showGunSpecsDialog() {
  gunSpecsDialog.value?.show()
}

function showShellSpecsDialog(shellName: string) {
  if (!gunSpecs.value) {
    return
  }
  shellSpecsDialog.value?.show(shellName, gunSpecs.value.availableShells[shellName]!, gunSpecs.value)
}

function getGunTitle(key: string) {
  return t(`names.guns.${key}`) + ' (' + t(`descriptions.guns.${key}.short`) + ')'
}

function getRestrictedAmount(config: AmmoConfig) {
  const intValue = parseInt(String(config.amount))
  const max = config.name === SGN_L_SHELL ? maxSgnShells.value : maxAmmo.value
  if (intValue > max) {
    return max
  }
  if (intValue < 0) {
    return 0
  }
  return intValue
}

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
        <vehicle-selector
            ref="vehicleSelector"
            no-settings
            @select="v => selectedVehicle = v"
        />
        <div v-show="selectedVehicle">
          <v-select
              v-model="config.gun"
              :items="guns"
              item-value="key"
              item-title="title"
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
          <div v-if="config.ammo">
            <div>
              {{ t('vehicleConfigs.selectShells') }}
              <span v-show="config.ammo!.length > 1">({{ t('vehicleConfigs.dragAndDropShellsTip') }})</span>
            </div>
            <v-list>
              <draggable
                  v-model="config.ammo"
                  group="shells"
                  item-key="name"
              >
                <template #item="{ element }">
                  <v-list-item class="mb-4 border-lg">
                    <div>
                      [{{ shellsOrderNumbers[element.name] }}] {{ t(`names.shells.${element.name}`) }}
                      <icon-btn
                          :icon="mdiInformationOutline"
                          :tooltip="t('common.specs')"
                          @click="showShellSpecsDialog(element.name)"
                      />
                    </div>
                    <v-slider
                        v-model="element.amount"
                        :max="element.name === SGN_L_SHELL ? maxSgnShells : maxAmmo"
                        :min="0"
                        class="align-center"
                        hide-details
                        @touchstart.stop
                        @mousedown.stop
                    >
                      <template v-slot:append>
                        <v-text-field
                            v-model="element.amount"
                            density="compact"
                            style="width: 90px"
                            type="number"
                            hide-details
                            single-line
                        ></v-text-field>
                      </template>
                    </v-slider>
                  </v-list-item>
                </template>
              </draggable>
            </v-list>
          </div>
          <div v-for="warning of warnings">
            <div style="color: #fb8c00; text-align: center;">{{ t('vehicleConfigs.warnings.' + warning) }}</div>
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
