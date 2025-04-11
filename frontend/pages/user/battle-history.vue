<script setup lang="ts">
import {useRouter} from "#app";
import type {PageResponse} from "~/data/response";
import type {UserBattleHistory} from "~/data/model";
import {useRequestErrorHandler} from "~/composables/request-error-handler";
import {ApiRequestSender} from "~/api/api-request-sender";
import type {PageRequest, SortRequest, UserBattleHistoryFiltersRequest} from "~/data/request";
import {DateUtils} from "~/utils/DateUtils";
import BattleHistoryFiltersForm from "~/components/battle-history-filters-form.vue";
import {useI18n} from "vue-i18n";

const {t} = useI18n()
const router = useRouter()

const currentPage = ref(1)
const itemsPerPage = ref(10)
const sort = ref<SortRequest | undefined>()
const filters = ref<UserBattleHistoryFiltersRequest | undefined>()
const historyPage = ref<PageResponse<UserBattleHistory>>({
  items: [],
  itemsLength: 0
})

const headers = computed(() => [
  {title: t('battleHistory.beginTime'), key: 'beginTime', align: 'start', sortable: true,
    value: item => DateUtils.getClientDateLocaleString(item.beginTime)},
  {title: t('commonHistory.battleType'), key: 'battleType', align: 'start', sortable: true,
    value: item => t('commonHistory.battleTypes.' + item.battleType)},
  {title: t('commonHistory.vehicle'), key: 'vehicleName', align: 'start', sortable: true,
    value: item => t(`names.vehicles.${item.vehicleName}`)},
  {title: t('battleHistory.survived'), key: 'survived', align: 'start', sortable: false,
    value: item => item.survived ? t('common.yes') : t('common.no')},
  {title: t('commonHistory.madeShots'), key: 'madeShots', align: 'end', sortable: true},
  {title: t('commonHistory.destroyedVehicles'), key: 'destroyedVehicles', align: 'end', sortable: true},
  {title: t('commonHistory.destroyedDrones'), key: 'destroyedDrones', align: 'end', sortable: true},
  {title: t('commonHistory.destroyedMissiles'), key: 'destroyedMissiles', align: 'end', sortable: true},
  {
    title: t('commonHistory.damage'), align: 'center', children: [
      {title: t('commonHistory.caused'), key: 'causedDamage', align: 'end', sortable: true,
        value: item => item.causedDamage.toFixed(2)},
      {title: t('commonHistory.received'), key: 'receivedDamage', align: 'end', sortable: true,
        value: item => item.receivedDamage.toFixed(2)}
    ]
  },
  {
    title: t('commonHistory.directHits'), align: 'center', children: [
      {title: t('commonHistory.caused'), key: 'causedDirectHits', align: 'end', sortable: true},
      {title: t('commonHistory.received'), key: 'receivedDirectHits', align: 'end', sortable: true}
    ]
  },
  {
    title: t('commonHistory.indirectHits'), align: 'center', children: [
      {title: t('commonHistory.caused'), key: 'causedIndirectHits', align: 'end', sortable: true},
      {title: t('commonHistory.received'), key: 'receivedIndirectHits', align: 'end', sortable: true}
    ]
  },
  {
    title: t('commonHistory.trackBreaks'), align: 'center', children: [
      {title: t('commonHistory.caused'), key: 'causedTrackBreaks', align: 'end', sortable: true},
      {title: t('commonHistory.received'), key: 'receivedTrackBreaks', align: 'end', sortable: true}
    ]
  },
])

watch([currentPage, itemsPerPage, sort, filters], loadHistoryPage)

onMounted(() => {
  loadHistoryPage()
})

function onDataTableOptionsUpdate(options) {
  itemsPerPage.value = options.itemsPerPage
  currentPage.value = options.page
  if (options.sortBy && options.sortBy.length) {
    const sortBy = options.sortBy[0]
    sort.value = {
      by: sortBy.key,
      dir: sortBy.order
    }
  } else {
    sort.value = undefined
  }
}

async function loadHistoryPage() {
  const pageRequest: PageRequest<UserBattleHistoryFiltersRequest> = {
    page: currentPage.value - 1,
    size: itemsPerPage.value
  }
  if (sort.value) {
    pageRequest.sort = sort.value
  }
  if (filters.value) {
    pageRequest.filters = filters.value
  }
  try {
    historyPage.value = await new ApiRequestSender().postJson<
        PageRequest<UserBattleHistoryFiltersRequest>, PageResponse<UserBattleHistory>
    >('/battles/history/page', pageRequest)
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
    <v-card width="100%" max-width="1200px">
      <v-card-title>
        Artillery online: {{ t('battleHistory.title') }}
      </v-card-title>
      <v-card-text>
        <battle-history-filters-form class="mb-4" @change="v => filters = v"/>
        <v-data-table-server
            class="mb-4"
            v-model:items-per-page="itemsPerPage"
            :headers="headers"
            :items="historyPage.items"
            :items-length="historyPage.itemsLength"
            :items-per-page-text="t('common.itemsPerPage')"
            :page-text="'{0}-{1} ' + t('common.of') + ' {2}'"
            item-value="battleHistoryId"
            @update:options="onDataTableOptionsUpdate"
            density="compact"
            fixed-header
        >
          <template v-slot:no-data>
            {{ t('common.noDataAvailable') }}
          </template>
        </v-data-table-server>
        <v-btn class="mb-4" width="100%" @click="back">{{ t('common.back') }}</v-btn>
      </v-card-text>
    </v-card>
  </NuxtLayout>
</template>
