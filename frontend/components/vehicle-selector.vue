<script setup lang="ts">
import {usePresetsStore} from "~/stores/presets";
import {useI18n} from "vue-i18n";
import type VehicleSpecsDialog from "~/components/vehicle-specs-dialog.vue";
import {mdiInformationOutline, mdiCogOutline} from "@mdi/js";
import IconBtn from "~/components/icon-btn.vue";
import {useRouter} from "#app";

const props = defineProps<{
  disabled?: boolean
  noSettings?: boolean
}>()

const emit = defineEmits(['select'])

const {t} = useI18n()
const router = useRouter()

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

function toVehicleConfigs() {
  router.push('/settings/vehicle-configs?selectedVehicle=' + selectedVehicle.value)
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
          v-if="!noSettings"
          :icon="mdiCogOutline"
          :tooltip="t('settings.vehicleConfigs')"
          @click="toVehicleConfigs"
      />
      <icon-btn
          :icon="mdiInformationOutline"
          :tooltip="t('common.specs')"
          @click="showSpecsDialog"
      />
    </template>
  </v-select>
  <vehicle-specs-dialog v-if="!!selectedVehicle" ref="specsDialog" :selected-vehicle="selectedVehicle"/>
</template>
