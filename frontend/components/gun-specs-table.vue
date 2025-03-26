<script setup lang="ts">
import {useI18n} from "vue-i18n";
import type {GunSpecs} from "~/playground/data/specs";

const props = defineProps<{
  gunSpecs: GunSpecs
}>()

const {t} = useI18n()

const openedPanels = ref<string[]>([])

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
        .reduce((a, b) => a + (a ? ', ' : '') + b, '')
  },
])

const shellNames = computed(() => {
  return Object.keys(props.gunSpecs.availableShells).sort()
})
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
  <v-expansion-panels class="mt-4" v-model="openedPanels">
    <template v-for="shellName of shellNames">
      <v-expansion-panel :value="shellName">
        <v-expansion-panel-title>
          {{ t('shellSpecsDialog.title') }}: {{ shellName }}
        </v-expansion-panel-title>
        <v-expansion-panel-text>
          <shell-specs-table :gun-specs="gunSpecs" :shell-specs="gunSpecs.availableShells[shellName]" />
        </v-expansion-panel-text>
      </v-expansion-panel>
    </template>
  </v-expansion-panels>
</template>
