<script setup lang="ts">
import {useI18n} from "vue-i18n";
import type {GunSpecs} from "~/playground/data/specs";

const props = defineProps<{
  gunSpecs: GunSpecs
}>()

const {t} = useI18n()

const specsToShow = computed(() => [
  {
    key: 'loadTime',
    value: props.gunSpecs.loadTime
  },
  {
    key: 'rotationVelocity',
    value: (180 * props.gunSpecs.rotationVelocity / Math.PI).toFixed(2)
  },
  {
    key: 'caliber',
    value: props.gunSpecs.caliber * 1000
  },
  {
    key: 'availableShells',
    value: Object.keys(props.gunSpecs.availableShells || {})
        .map(key => t(`names.shells.${key}`))
        .reduce((a, b) => a + (a ? ', ' : '') + b, '')
  },
])
</script>

<template>
  <v-table density="compact">
    <tbody>
    <tr v-for="spec of specsToShow">
      <td>{{ t('gunSpecsDialog.' + spec.key) }}</td>
      <td>{{ spec.value }}</td>
    </tr>
    </tbody>
  </v-table>
</template>
