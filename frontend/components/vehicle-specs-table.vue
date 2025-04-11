<script setup lang="ts">
import {useI18n} from "vue-i18n";
import type {VehicleSpecs} from "~/playground/data/specs";

const props = defineProps<{
  vehicleSpecs: VehicleSpecs
}>()

const {t} = useI18n()

const specsToShow = computed(() => [
  {
    key: 'hitPoints',
    value: props.vehicleSpecs.hitPoints
  },
  {
    key: 'trackRepairTime',
    value: props.vehicleSpecs.trackRepairTime
  },
  {
    key: 'availableGuns',
    value: Object.keys(props.vehicleSpecs.availableGuns || {})
        .map(key => t(`names.guns.${key}`))
        .reduce((a, b) => a + (a ? ', ' : '') + b, '')
  },
])
</script>

<template>
  <v-table density="compact">
    <tbody>
    <tr v-for="spec of specsToShow">
      <td>{{ t('vehicleSpecsDialog.' + spec.key) }}</td>
      <td>{{ spec.value }}</td>
    </tr>
    </tbody>
  </v-table>
</template>
