<script setup lang="ts">
import {ref} from 'vue'
import {useI18n} from "vue-i18n";
import type {GunSpecs, VehicleSpecs} from "~/playground/data/specs";
import {usePresetsStore} from "~/stores/presets";
import VehicleSpecsTable from "~/components/vehicle-specs-table.vue";
import {useConfigsStore} from "~/stores/configs";
import type {UserVehicleConfig} from "~/data/model";

const props = defineProps<{
  selectedVehicle: string
}>()

const {t} = useI18n()
const presetsStore = usePresetsStore()
const configsStore = useConfigsStore()

const opened = ref(false)

const vehicleConfig = computed<UserVehicleConfig | undefined>(() => {
  return configsStore.vehicleConfigs[props.selectedVehicle]
})

const vehicleSpecs = computed<VehicleSpecs>(() => {
  return presetsStore.vehicles[props.selectedVehicle]
})

const gunSpecs = computed<GunSpecs | undefined>(() => {
  if (!vehicleConfig.value?.gun) {
    return undefined
  }
  return vehicleSpecs.value.availableGuns[vehicleConfig.value.gun]
})

async function loadVehicleConfig() {
  try {
    await configsStore.loadVehicleConfig(vehicleSpecs.value!)
  } catch (ignore) {
  }
}

function show() {
  opened.value = true
  if (!vehicleConfig.value) {
    loadVehicleConfig()
  }
}

function hide() {
  opened.value = false
}

defineExpose({
  show
})
</script>

<template>
  <v-dialog :model-value="opened" :persistent="true" max-width="600px">
    <v-card width="100%">
      <v-card-title>{{ t('vehicleSpecsDialog.title') }}: {{ vehicleSpecs.name }}</v-card-title>
      <v-card-text>
        <vehicle-specs-table :vehicle-specs="vehicleSpecs"/>
        <div class="mt-4" v-if="gunSpecs">
          <h4>{{ t('gunSpecsDialog.title') }}: {{ vehicleConfig.gun }}</h4>
          <gun-shells-specs-tables class="mt-4" :gun-specs="gunSpecs"/>
        </div>
        <div class="d-flex mt-4">
          <v-btn color="primary" @click="hide">{{ t('common.ok') }}</v-btn>
        </div>
      </v-card-text>
    </v-card>
  </v-dialog>
</template>
