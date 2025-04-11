<script setup lang="ts">
import {usePresetsStore} from "~/stores/presets";
import {useI18n} from "vue-i18n";
import type VehicleSpecsDialog from "~/components/vehicle-specs-dialog.vue";
import {mdiInformationOutline} from "@mdi/js";
import IconBtn from "~/components/icon-btn.vue";

const props = defineProps<{
  disabled?: boolean
}>()

const emit = defineEmits(['select'])

const {t} = useI18n()

const specsDialog = ref<InstanceType<typeof VehicleSpecsDialog> | null>(null)

const presetsStore = usePresetsStore()

const selectedVehicle = ref<string>()

const vehicles = computed(() => {
  return Object.keys(presetsStore.vehicles)
      .map(key => ({key, title: getTitle(key)}))
})

watch(selectedVehicle, value => {
  emit('select', value)
})

function setSelectedVehicle(value: string) {
  selectedVehicle.value = value
}

function showSpecsDialog() {
  specsDialog.value?.show()
}

function getTitle(key: string) {
  return t(`names.vehicles.${key}`) + ' (' + t(`descriptions.vehicles.${key}.short`) + ')'
}

defineExpose({
  setSelectedVehicle
})
</script>

<template>
  <v-select
      v-model="selectedVehicle"
      :items="vehicles"
      item-value="key"
      item-title="title"
      :disabled="props.disabled"
      density="compact"
      :label="t('vehicleSelector.selectVehicle')"
      clearable
  >
    <template v-if="!!selectedVehicle" v-slot:append>
      <icon-btn
          :icon="mdiInformationOutline"
          :tooltip="t('common.specs')"
          @click="showSpecsDialog"
      />
    </template>
  </v-select>
  <vehicle-specs-dialog v-if="!!selectedVehicle" ref="specsDialog" :selected-vehicle="selectedVehicle"/>
</template>
