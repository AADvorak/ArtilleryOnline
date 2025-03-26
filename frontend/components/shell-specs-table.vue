<script setup lang="ts">
import {useI18n} from "vue-i18n";
import type {GunSpecs, ShellSpecs} from "~/playground/data/specs";
import {ShellType} from "~/playground/data/common";

const {t} = useI18n()

const props = defineProps<{
  gunSpecs: GunSpecs
  shellSpecs: ShellSpecs
}>()

const specsToShow = computed(() => [
  {
    key: 'damage',
    value: calculateDamage()
  },
  {
    key: 'dpm',
    value: (60 * calculateDamage() / props.gunSpecs.loadTime).toFixed(0)
  },
  {
    key: 'velocity',
    value: props.shellSpecs.velocity
  },
  {
    key: 'caliber',
    value: props.shellSpecs.caliber * 1000
  },
  {
    key: 'type',
    value: t('shellSpecsDialog.types.' + props.shellSpecs.type)
  },
])

function calculateDamage() {
  const damage = props.shellSpecs.damage
  return props.shellSpecs.type === ShellType.BMB ? 2 * damage : damage
}
</script>

<template>
  <v-table density="compact">
    <tbody>
    <tr v-for="spec of specsToShow">
      <td>{{ t('shellSpecsDialog.' + spec.key) }}</td>
      <td>{{ spec.value }}</td>
    </tr>
    </tbody>
  </v-table>
</template>
