<script setup lang="ts">
import {useRouter} from "#app";
import type {PageResponse} from "~/data/response";
import type {UserBattleHistory} from "~/data/model";
import {useRequestErrorHandler} from "~/composables/request-error-handler";
import {ApiRequestSender} from "~/api/api-request-sender";
import type {PageRequest, SortRequest, UserBattleHistoryFiltersRequest} from "~/data/request";
import {DateUtils} from "~/utils/DateUtils";
import {BattleType} from "~/playground/data/battle";
import {usePresetsStore} from "~/stores/presets";
import DatePickerMenu from "~/components/date-picker-menu.vue";

const router = useRouter()
const presetsStore = usePresetsStore()

const currentPage = ref(1)
const itemsPerPage = ref(10)
const sort = ref<SortRequest | undefined>()
const selectedBattleType = ref<BattleType | undefined>()
const selectedVehicleName = ref<string | undefined>()
const selectedDtFrom = ref<Date | undefined>()
const selectedDtTo = ref<Date | undefined>()
const battleTypes = ref([BattleType.RANDOM, BattleType.ROOM])
const historyPage = ref<PageResponse<UserBattleHistory>>({
  items: [],
  itemsLength: 0
})
const headers = ref([
  {title: 'Begin time', key: 'beginTime', align: 'start', sortable: true,
    value: item => DateUtils.getClientDateLocaleString(item.beginTime)},
  {title: 'Type', key: 'battleType', align: 'start', sortable: true},
  {title: 'Vehicle name', key: 'vehicleName', align: 'start', sortable: true},
  {title: 'Survive', key: 'survived', align: 'start', sortable: false,
    value: item => item.survived ? 'Yes' : 'No'},
  {title: 'Made shots', key: 'madeShots', align: 'end', sortable: true},
  {title: 'Destroyed vehicles', key: 'destroyedVehicles', align: 'end', sortable: true},
  {
    title: 'Damage', align: 'center', children: [
      {title: 'Caused', key: 'causedDamage', align: 'end', sortable: true,
        value: item => item.causedDamage.toFixed(2)},
      {title: 'Received', key: 'receivedDamage', align: 'end', sortable: true,
        value: item => item.receivedDamage.toFixed(2)}
    ]
  },
  {
    title: 'Direct hits', align: 'center', children: [
      {title: 'Caused', key: 'causedDirectHits', align: 'end', sortable: true},
      {title: 'Received', key: 'receivedDirectHits', align: 'end', sortable: true}
    ]
  },
  {
    title: 'Indirect hits', align: 'center', children: [
      {title: 'Caused', key: 'causedIndirectHits', align: 'end', sortable: true},
      {title: 'Received', key: 'receivedIndirectHits', align: 'end', sortable: true}
    ]
  },
  {
    title: 'Track breaks', align: 'center', children: [
      {title: 'Caused', key: 'causedTrackBreaks', align: 'end', sortable: true},
      {title: 'Received', key: 'receivedTrackBreaks', align: 'end', sortable: true}
    ]
  },
])

const vehicles = computed(() => {
  return Object.keys(presetsStore.vehicles)
})

watch([selectedBattleType, selectedVehicleName, selectedDtFrom, selectedDtTo], loadHistoryPage)

function onDataTableOptionsUpdate(options) {
  itemsPerPage.value = options.itemsPerPage
  currentPage.value = options.page
  if (options.sortBy && options.sortBy.length) {
    const sortBy = options.sortBy[0]
    sort.value = {
      by: sortBy.key,
      dir: sortBy.order
    }
  }
  loadHistoryPage()
}

async function loadHistoryPage() {
  const pageRequest: PageRequest<UserBattleHistoryFiltersRequest> = {
    page: currentPage.value - 1,
    size: itemsPerPage.value
  }
  if (sort.value) {
    pageRequest.sort = sort.value
  }
  pageRequest.filters = createFilters()
  try {
    historyPage.value = await new ApiRequestSender().postJson<
        PageRequest<UserBattleHistoryFiltersRequest>, PageResponse<UserBattleHistory>
    >('/battles/history/page', pageRequest)
  } catch (e) {
    useRequestErrorHandler().handle(e)
  }
}

function createFilters() {
  const filters: UserBattleHistoryFiltersRequest = {}
  if (selectedBattleType.value) {
    filters.battleType = selectedBattleType.value
  }
  if (selectedVehicleName.value) {
    filters.vehicleName = selectedVehicleName.value
  }
  if (selectedDtFrom.value) {
    filters.dtFrom = DateUtils.getISOString(selectedDtFrom.value)
  }
  if (selectedDtTo.value) {
    filters.dtTo = DateUtils.getISOStringPlusDay(selectedDtTo.value)
  }
  return filters
}

function back() {
  router.push('/user')
}
</script>

<template>
  <NuxtLayout>
    <v-card width="100%" max-width="1200px">
      <v-card-title>
        Artillery online: user / battle history
      </v-card-title>
      <v-card-text>
        <v-form class="mb-4">
          <v-row>
            <v-col>
              <v-select
                  v-model="selectedBattleType"
                  :items="battleTypes"
                  density="compact"
                  label="Battle type"
                  clearable
              />
            </v-col>
            <v-col>
              <v-select
                  v-model="selectedVehicleName"
                  :items="vehicles"
                  density="compact"
                  label="Select vehicle"
                  clearable
              />
            </v-col>
          </v-row>
          <v-row>
            <v-col>
              <date-picker-menu name="Date from" @select="v => selectedDtFrom = v"/>
            </v-col>
            <v-col>
              <date-picker-menu name="Date to" @select="v => selectedDtTo = v"/>
            </v-col>
          </v-row>
        </v-form>
        <v-data-table-server
            class="mb-4"
            v-model:items-per-page="itemsPerPage"
            :headers="headers"
            :items="historyPage.items"
            :items-length="historyPage.itemsLength"
            item-value="battleHistoryId"
            @update:options="onDataTableOptionsUpdate"
            density="compact"
            fixed-header
        />
        <v-btn class="mb-4" width="100%" @click="back">Back</v-btn>
      </v-card-text>
    </v-card>
  </NuxtLayout>
</template>
