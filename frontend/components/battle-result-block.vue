<script setup lang="ts">
import type {UserBattleResult} from "~/data/model";
import {useI18n} from "vue-i18n";

const props = defineProps<{
  result: UserBattleResult
}>()

const {t} = useI18n()

const config = ref([
  {localeKey: 'battleHistory.survived', value: props.result.survived ? t('common.yes') : t('common.no')},
  {localeKey: 'commonHistory.madeShots', value: props.result.madeShots},
  {localeKey: 'commonHistory.destroyedVehicles', value: props.result.destroyedVehicles},
  {localeKey: 'commonHistory.destroyedDrones', value: props.result.destroyedDrones},
  {localeKey: 'commonHistory.destroyedMissiles', value: props.result.destroyedMissiles},
])

const causedReceivedConfig = ref([
  {localeKey: 'commonHistory.damage', causedValue: props.result.causedDamage.toFixed(2),
    receivedValue: props.result.receivedDamage.toFixed(2)},
  {localeKey: 'commonHistory.directHits', causedValue: props.result.causedDirectHits,
    receivedValue: props.result.receivedDirectHits},
  {localeKey: 'commonHistory.indirectHits', causedValue: props.result.causedIndirectHits,
    receivedValue: props.result.receivedIndirectHits},
  {localeKey: 'commonHistory.trackBreaks', causedValue: props.result.causedTrackBreaks,
    receivedValue: props.result.receivedTrackBreaks},
])
</script>

<template>
  <v-table density="compact">
    <tbody>
    <template v-for="item of config">
      <tr v-if="item.value">
        <td>{{ t(item.localeKey) }}</td>
        <td class="number-column">{{ item.value }}</td>
      </tr>
    </template>
    </tbody>
  </v-table>
  <v-table density="compact">
    <thead>
    <tr>
      <th></th>
      <th class="text-right">{{ t('commonHistory.caused') }}</th>
      <th class="text-right">{{ t('commonHistory.received') }}</th>
    </tr>
    </thead>
    <tbody>
    <template v-for="item of causedReceivedConfig">
      <tr v-if="item.causedValue || item.receivedValue">
        <td>{{ t(item.localeKey) }}</td>
        <td class="number-column">{{ item.causedValue }}</td>
        <td class="number-column">{{ item.receivedValue }}</td>
      </tr>
    </template>
    </tbody>
  </v-table>
</template>

<style scoped>
.number-column {
  text-align: right;
}
</style>
