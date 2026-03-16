<script setup lang="ts">
import {usePresetsStore} from "~/stores/presets";
import {useI18n} from "vue-i18n";
import type VehicleSpecsDialog from "~/components/vehicle-specs-dialog.vue";
import {mdiInformationOutline, mdiCogOutline} from "@mdi/js";
import IconBtn from "~/components/icon-btn.vue";
import {useRouter} from "#app";

const RANDOM_KEY = 'random'

const props = defineProps<{
  disabled?: boolean
  noSettings?: boolean
  noInfo?: boolean
  hideDetails?: boolean
  isRandomVehicle?: boolean
}>()

const emit = defineEmits(['select'])

const {t} = useI18n()
const router = useRouter()

const specsDialog = ref<InstanceType<typeof VehicleSpecsDialog> | null>(null)

const presetsStore = usePresetsStore()

const selectedVehicle = ref<string>()

const vehicles = computed(() => {
  const output = Object.keys(presetsStore.vehicles || {})
      .map(key => ({key, title: getTitle(key)}))
  if (props.isRandomVehicle) {
    output.unshift({key: RANDOM_KEY, title: t('vehicleSelector.randomVehicle')})
  }
  return output
})

onMounted(() => {
  if (props.isRandomVehicle) {
    selectedVehicle.value = RANDOM_KEY
  }
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
      :hide-details="props.hideDetails"
      :clearable="!props.isRandomVehicle"
  >
    <template v-if="!!selectedVehicle && selectedVehicle !== RANDOM_KEY" v-slot:append>
      <icon-btn
          v-if="!noSettings"
          :icon="mdiCogOutline"
          :tooltip="t('settings.vehicleConfigs')"
          @click="toVehicleConfigs"
      />
      <icon-btn
          v-if="!noInfo"
          :icon="mdiInformationOutline"
          :tooltip="t('common.specs')"
          @click="showSpecsDialog"
      />
    </template>
  </v-select>
  <vehicle-specs-dialog
      v-if="!!selectedVehicle && selectedVehicle !== RANDOM_KEY"
      ref="specsDialog"
      :selected-vehicle="selectedVehicle"
  />
</template>
