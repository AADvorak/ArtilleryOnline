<script setup lang="ts">
import {BattleType} from "~/playground/data/battle";
import {useI18n} from "vue-i18n";

const battleType = defineModel<BattleType | undefined>()

const props = defineProps<{
  availableTypes: BattleType[]
  clearable?: boolean
}>()

const {t} = useI18n()

const battleTypes = computed(() => props.availableTypes
    .map(key => ({key, name: t('commonHistory.battleTypes.' + key)})))
</script>

<template>
  <v-select
      v-model="battleType"
      :items="battleTypes"
      item-value="key"
      item-title="name"
      density="compact"
      :label="t('commonHistory.battleType')"
      :clearable="clearable"
  />
</template>
