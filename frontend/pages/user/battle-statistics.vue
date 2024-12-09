<script setup lang="ts">
import {useRouter} from "#app";
import type {UserBattleHistoryFiltersRequest} from "~/data/request";
import type {UserBattleStatistics, UserBattleStatisticsCoefficients, UserBattleStatisticsPerBattle} from "~/data/model";
import BattleHistoryFiltersForm from "~/components/battle-history-filters-form.vue";
import {ApiRequestSender} from "~/api/api-request-sender";
import {useRequestErrorHandler} from "~/composables/request-error-handler";
import {useStatisticsCalculator} from "~/composables/statistics-calculator";
import {useI18n} from "vue-i18n";

const {t} = useI18n()
const router = useRouter()

const sumsConfig = ref([
  {key: 'battlesPlayed', name: t('battleStatistics.battlesPlayed')},
  {key: 'battlesSurvived', name: t('battleStatistics.battlesSurvived')},
  {key: 'madeShots', name: t('commonHistory.madeShots')},
  {key: 'destroyedVehicles', name: t('commonHistory.destroyedVehicles')},
])
const sumsCausedReceivedConfig = ref([
  {causedKey: 'causedDamage', receivedKey: 'receivedDamage', name: t('commonHistory.damage'),
    format: value => value.toFixed(2)},
  {causedKey: 'causedDirectHits', receivedKey: 'receivedDirectHits', name: t('commonHistory.directHits'),
    format: value => value},
  {causedKey: 'causedIndirectHits', receivedKey: 'receivedIndirectHits', name: t('commonHistory.indirectHits'),
    format: value => value},
  {causedKey: 'causedTrackBreaks', receivedKey: 'receivedTrackBreaks', name: t('commonHistory.trackBreaks'),
    format: value => value},
])
const perBattleConfig = ref([
  {key: 'madeShots', name: t('commonHistory.madeShots')},
  {key: 'destroyedVehicles', name: t('commonHistory.destroyedVehicles')},
])
const perBattleCausedReceivedConfig = ref([
  {causedKey: 'causedDamage', receivedKey: 'receivedDamage', name: t('commonHistory.damage')},
  {causedKey: 'causedDirectHits', receivedKey: 'receivedDirectHits', name: t('commonHistory.directHits')},
  {causedKey: 'causedIndirectHits', receivedKey: 'receivedIndirectHits', name: t('commonHistory.indirectHits')},
  {causedKey: 'causedTrackBreaks', receivedKey: 'receivedTrackBreaks', name: t('commonHistory.trackBreaks')},
])
const coefficientsConfig = ref([
  {key: 'survivalRate', fractionDigits: 0},
  {key: 'directHitRate', fractionDigits: 0},
  {key: 'indirectHitRate', fractionDigits: 0},
  {key: 'trackBreakRate', fractionDigits: 0},
  {key: 'damagePerShot', fractionDigits: 2},
])

const filters = ref<UserBattleHistoryFiltersRequest | undefined>()
const statistics = ref<UserBattleStatistics | undefined>()
const perBattleStatistics = ref<UserBattleStatisticsPerBattle | undefined>()
const coefficients = ref<UserBattleStatisticsCoefficients | undefined>()
const openedPanels = ref<string[]>(['total'])

watch(filters, loadStatistics)

onMounted(() => {
  loadStatistics()
})

async function loadStatistics() {
  try {
    statistics.value = await new ApiRequestSender().postJson<
        UserBattleHistoryFiltersRequest, UserBattleStatistics
    >('/battles/statistics/filter', filters.value || {})
    perBattleStatistics.value = useStatisticsCalculator().calculatePerBattle(statistics.value)
    coefficients.value = useStatisticsCalculator().calculateCoefficients(statistics.value)
  } catch (e) {
    useRequestErrorHandler().handle(e)
  }
}

function back() {
  router.push('/user')
}
</script>

<template>
  <NuxtLayout>
    <v-card width="100%" max-width="600px">
      <v-card-title>
        Artillery online: {{ t('battleStatistics.title') }}
      </v-card-title>
      <v-card-text>
        <battle-history-filters-form class="mb-4" @change="v => filters = v"/>
        <v-expansion-panels v-if="statistics" class="mb-4" v-model="openedPanels">
          <v-expansion-panel value="total">
            <v-expansion-panel-title>
              {{ t('battleStatistics.total') }}
            </v-expansion-panel-title>
            <v-expansion-panel-text>
              <v-table density="compact">
                <tbody>
                <tr v-for="sum of sumsConfig">
                  <td>{{ sum.name }}</td>
                  <td>{{ statistics[sum.key] }}</td>
                </tr>
                </tbody>
              </v-table>
              <v-table density="compact">
                <thead>
                <tr>
                  <th></th>
                  <th>{{ t('commonHistory.caused') }}</th>
                  <th>{{ t('commonHistory.received') }}</th>
                </tr>
                </thead>
                <tbody>
                <tr v-for="sum of sumsCausedReceivedConfig">
                  <td>{{ sum.name }}</td>
                  <td>{{ sum.format(statistics[sum.causedKey]) }}</td>
                  <td>{{ sum.format(statistics[sum.receivedKey]) }}</td>
                </tr>
                </tbody>
              </v-table>
            </v-expansion-panel-text>
          </v-expansion-panel>
          <v-expansion-panel v-if="perBattleStatistics" value="perBattle">
            <v-expansion-panel-title>
              {{ t('battleStatistics.perBattle') }}
            </v-expansion-panel-title>
            <v-expansion-panel-text>
              <v-table density="compact">
                <tbody>
                <tr v-for="item of perBattleConfig">
                  <td>{{ item.name }}</td>
                  <td>{{ perBattleStatistics[item.key].toFixed(2) }}</td>
                </tr>
                </tbody>
              </v-table>
              <v-table density="compact">
                <thead>
                <tr>
                  <th></th>
                  <th>{{ t('commonHistory.caused') }}</th>
                  <th>{{ t('commonHistory.received') }}</th>
                </tr>
                </thead>
                <tbody>
                <tr v-for="item of perBattleCausedReceivedConfig">
                  <td>{{ item.name }}</td>
                  <td>{{ perBattleStatistics[item.causedKey].toFixed(2) }}</td>
                  <td>{{ perBattleStatistics[item.receivedKey].toFixed(2) }}</td>
                </tr>
                </tbody>
              </v-table>
            </v-expansion-panel-text>
          </v-expansion-panel>
          <v-expansion-panel v-if="coefficients" value="coefficients">
            <v-expansion-panel-title>
              {{ t('battleStatistics.coefficients') }}
            </v-expansion-panel-title>
            <v-expansion-panel-text>
              <v-table density="compact">
                <tbody>
                <tr v-for="item of coefficientsConfig">
                  <td>{{ t('battleStatistics.' + item.key) }}</td>
                  <td>{{ coefficients[item.key].toFixed(item.fractionDigits) }}</td>
                </tr>
                </tbody>
              </v-table>
            </v-expansion-panel-text>
          </v-expansion-panel>
        </v-expansion-panels>
        <v-btn class="mb-4" width="100%" @click="back">Back</v-btn>
      </v-card-text>
    </v-card>
  </NuxtLayout>
</template>
