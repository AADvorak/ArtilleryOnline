<script setup lang="ts">
import {useRouter} from "#app";
import type {UserBattleHistoryFiltersRequest} from "~/data/request";
import type {UserBattleStatistics} from "~/data/model";
import BattleHistoryFiltersForm from "~/components/battle-history-filters-form.vue";
import {ApiRequestSender} from "~/api/api-request-sender";
import {useRequestErrorHandler} from "~/composables/request-error-handler";

const router = useRouter()

const sumsConfig = ref([
  {key: 'battlesPlayed', name: 'Battles played'},
  {key: 'battlesSurvived', name: 'Battles survived'},
  {key: 'madeShots', name: 'Made shots'},
  {key: 'destroyedVehicles', name: 'Destroyed vehicles'},
])
const sumsCausedReceivedConfig = ref([
  {causedKey: 'causedDamage', receivedKey: 'receivedDamage', name: 'Damage',
    format: value => value.toFixed(2)},
  {causedKey: 'causedDirectHits', receivedKey: 'receivedDirectHits', name: 'Direct hits',
    format: value => value},
  {causedKey: 'causedIndirectHits', receivedKey: 'receivedIndirectHits', name: 'Indirect hits',
    format: value => value},
  {causedKey: 'causedTrackBreaks', receivedKey: 'receivedTrackBreaks', name: 'Track breaks',
    format: value => value},
])

const filters = ref<UserBattleHistoryFiltersRequest | undefined>()
const statistics = ref<UserBattleStatistics | undefined>()

watch(filters, loadStatistics)

onMounted(() => {
  loadStatistics()
})

async function loadStatistics() {
  try {
    statistics.value = await new ApiRequestSender().postJson<
        UserBattleHistoryFiltersRequest, UserBattleStatistics
    >('/battles/statistics/filter', filters.value || {})
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
        Artillery online: user / battle statistics
      </v-card-title>
      <v-card-text>
        <battle-history-filters-form class="mb-4" @change="v => filters = v"/>
        <template v-if="statistics">
          <v-table class="mb-4" density="compact">
            <tbody>
            <tr v-for="sum of sumsConfig">
              <td>{{ sum.name }}</td>
              <td>{{ statistics[sum.key] }}</td>
            </tr>
            </tbody>
          </v-table>
          <v-table class="mb-4" density="compact">
            <thead>
            <tr>
              <th></th>
              <th>Caused</th>
              <th>Received</th>
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
        </template>
        <v-btn class="mb-4" width="100%" @click="back">Back</v-btn>
      </v-card-text>
    </v-card>
  </NuxtLayout>
</template>
