<script setup lang="ts">
import {useI18n} from "vue-i18n";
import type {Armor, VehicleSpecs} from "~/playground/data/specs";
import {HitSurface} from "~/playground/data/common";

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
    key: 'armor',
    value: formatArmor(props.vehicleSpecs.armor)
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

function formatArmor(armor: Armor) {
  return `${armor[HitSurface.SIDE]} / ${armor[HitSurface.TOP]} / ${armor[HitSurface.BOTTOM]}`
}
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
