<script setup lang="ts">
import {BattleType} from "~/playground/data/battle";
import type {DateRange} from "~/data/model";
import {DateUtils} from "~/utils/DateUtils";
import {usePresetsStore} from "~/stores/presets";
import type {UserBattleHistoryFiltersRequest} from "~/data/request";
import {useI18n} from "vue-i18n";

const DATE_MODES = {
  TODAY: 'today',
  LAST_WEEK: 'lastWeek',
  LAST_MONTH: 'lastMonth',
  SPECIFIED_DATE: 'specifiedDate',
  SPECIFIED_DATE_RANGE: 'specifiedDateRange'
}

const emit = defineEmits(['change'])

const {t} = useI18n()
const presetsStore = usePresetsStore()

const selectedBattleType = ref<BattleType | undefined>()
const selectedVehicleName = ref<string | undefined>()
const selectedDateRange = ref<DateRange | undefined>()
const selectedDateMode = ref<string | undefined>()

const battleTypes = computed(() => [BattleType.RANDOM, BattleType.ROOM]
    .map(key => ({key, name: t('commonHistory.battleTypes.' + key)})))
const vehicles = computed(() => {
  return Object.keys(presetsStore.vehicles)
})
const dateModes = computed(() => {
  return Object.values(DATE_MODES).map(key => ({key, name: t('battleHistoryFiltersForm.' + key)}))
})

watch([selectedBattleType, selectedVehicleName, selectedDateRange], () => {
  emit('change', createFilters())
})
watch(selectedDateMode, value => {
  const todayBegin = DateUtils.getTodayBegin()
  switch (value) {
    case DATE_MODES.TODAY:
      selectedDateRange.value = {
        from: todayBegin,
        to: DateUtils.getPlusDay(todayBegin)
      }
      break
    case DATE_MODES.LAST_WEEK:
      selectedDateRange.value = {
        from: DateUtils.getMinusDays(todayBegin, 7),
        to: DateUtils.getPlusDay(todayBegin)
      }
      break
    case DATE_MODES.LAST_MONTH:
      selectedDateRange.value = {
        from: DateUtils.getMinusDays(todayBegin, 30),
        to: DateUtils.getPlusDay(todayBegin)
      }
      break
    default:
      selectedDateRange.value = undefined
  }
})

function createFilters() {
  const filters: UserBattleHistoryFiltersRequest = {}
  if (selectedBattleType.value) {
    filters.battleType = selectedBattleType.value
  }
  if (selectedVehicleName.value) {
    filters.vehicleName = selectedVehicleName.value
  }
  if (selectedDateRange.value) {
    filters.dtFrom = DateUtils.getISOString(selectedDateRange.value.from)
    filters.dtTo = DateUtils.getISOString(selectedDateRange.value.to)
  }
  return filters
}

function onDateSelect(date: Date) {
  if (date) {
    selectedDateRange.value = {
      from: date,
      to: DateUtils.getPlusDay(date)
    }
  } else {
    selectedDateRange.value = undefined
  }
}
</script>

<template>
  <v-form>
    <v-row no-gutters>
      <v-col>
        <v-select
            v-model="selectedBattleType"
            :items="battleTypes"
            item-value="key"
            item-title="name"
            density="compact"
            :label="t('commonHistory.battleType')"
            clearable
        />
      </v-col>
      <v-col class="ml-4">
        <v-select
            v-model="selectedVehicleName"
            :items="vehicles"
            density="compact"
            :label="t('commonHistory.vehicle')"
            clearable
        />
      </v-col>
    </v-row>
    <v-row no-gutters>
      <v-col>
        <v-select
            v-model="selectedDateMode"
            :items="dateModes"
            item-value="key"
            item-title="name"
            density="compact"
            :label="t('battleHistoryFiltersForm.dateFilteringMode')"
            clearable
        />
      </v-col>
      <v-col class="ml-4" v-show="selectedDateMode === DATE_MODES.SPECIFIED_DATE">
        <date-picker-select :name="t('battleHistoryFiltersForm.date')" @select="onDateSelect"/>
      </v-col>
      <v-col class="ml-4" v-show="selectedDateMode === DATE_MODES.SPECIFIED_DATE_RANGE">
        <date-range-picker-select :name="t('battleHistoryFiltersForm.dateRange')" @select="v => selectedDateRange = v"/>
      </v-col>
    </v-row>
  </v-form>
</template>
