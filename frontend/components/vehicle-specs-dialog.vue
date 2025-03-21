<script setup lang="ts">
import {ref} from 'vue'
import {useI18n} from "vue-i18n";
import type {VehicleSpecs} from "~/playground/data/specs";
import {usePresetsStore} from "~/stores/presets";

const props = defineProps<{
  selectedVehicle: string | undefined
}>()

const {t} = useI18n()
const presetsStore = usePresetsStore()

const opened = ref(false)

const vehicleSpecs = computed<VehicleSpecs | undefined>(() => {
  if (!props.selectedVehicle) {
    return undefined
  }
  return presetsStore.vehicles[props.selectedVehicle] as VehicleSpecs
})
const specsToShow = computed(() => [
  {
    key: 'hitPoints',
    value: vehicleSpecs.value?.hitPoints
  },
  {
    key: 'trackRepairTime',
    value: vehicleSpecs.value?.trackRepairTime
  },
  {
    key: 'availableGuns',
    value: Object.keys(vehicleSpecs.value?.availableGuns || {})
        .reduce((a, b) => a + (a ? ', ' : '') + b, '')
  },
])

function show() {
  opened.value = true
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
    <v-card v-if="!!vehicleSpecs" width="100%">
      <v-card-title>{{ t('vehicleSpecsDialog.title') }}: {{ vehicleSpecs.name }}</v-card-title>
      <v-card-text>
        <v-table density="compact">
          <tbody>
          <tr v-for="spec of specsToShow">
            <td>{{ t('vehicleSpecsDialog.' + spec.key) }}</td>
            <td>{{ spec.value }}</td>
          </tr>
          </tbody>
        </v-table>
        <div class="d-flex mt-4">
          <v-btn color="primary" @click="hide">{{ t('common.ok') }}</v-btn>
        </div>
      </v-card-text>
    </v-card>
  </v-dialog>
</template>
