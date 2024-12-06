<script setup lang="ts">
import {usePresetsStore} from "~/stores/presets";
import {useI18n} from "vue-i18n";

const props = defineProps<{
  disabled?: boolean
}>()

const emit = defineEmits(['select'])

const {t} = useI18n()

const presetsStore = usePresetsStore()

const selectedVehicle = ref<string>()

const vehicles = computed(() => {
  return Object.keys(presetsStore.vehicles)
})

watch(selectedVehicle, value => {
  emit('select', value)
})

function setSelectedVehicle(value: string) {
  selectedVehicle.value = value
}

defineExpose({
  setSelectedVehicle
})
</script>

<template>
  <v-select
      v-model="selectedVehicle"
      :items="vehicles"
      :disabled="props.disabled"
      density="compact"
      :label="t('vehicleSelector.selectVehicle')"
  />
</template>
