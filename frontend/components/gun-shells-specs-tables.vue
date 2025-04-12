<script setup lang="ts">
import {useI18n} from "vue-i18n";
import type {GunSpecs} from "~/playground/data/specs";

const props = defineProps<{
  gunSpecs: GunSpecs
}>()

const {t} = useI18n()

const openedPanels = ref<string[]>([])

const shellNames = computed(() => {
  return Object.keys(props.gunSpecs.availableShells).sort()
})
</script>

<template>
  <gun-specs-table :gun-specs="gunSpecs"/>
  <v-expansion-panels class="mt-4" v-model="openedPanels">
    <template v-for="shellName of shellNames">
      <v-expansion-panel :value="shellName">
        <v-expansion-panel-title>
          {{ t('shellSpecsDialog.title') }}: {{ t(`names.shells.${shellName}`) }}
        </v-expansion-panel-title>
        <v-expansion-panel-text>
          <shell-specs-table :gun-specs="gunSpecs" :shell-specs="gunSpecs.availableShells[shellName]" />
        </v-expansion-panel-text>
      </v-expansion-panel>
    </template>
  </v-expansion-panels>
</template>
