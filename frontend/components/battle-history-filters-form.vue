<script setup lang="ts">
import {BattleType} from "~/playground/data/battle";
import type {DateRange} from "~/data/model";
import {DateUtils} from "~/utils/DateUtils";
import {usePresetsStore} from "~/stores/presets";
import type {UserBattleHistoryFiltersRequest} from "~/data/request";

const DATE_MODES = {
  TODAY: 'Today',
  LAST_WEEK: 'Last week',
  LAST_MONTH: 'Last month',
  SPECIFIC_DATE: 'Specific date',
  SPECIFIC_DATE_RANGE: 'Specific date range'
}

const emit = defineEmits(['change'])

const presetsStore = usePresetsStore()

const selectedBattleType = ref<BattleType | undefined>()
const selectedVehicleName = ref<string | undefined>()
const selectedDateRange = ref<DateRange | undefined>()
const selectedDateMode = ref<string | undefined>()
const battleTypes = ref([BattleType.RANDOM, BattleType.ROOM])

const vehicles = computed(() => {
  return Object.keys(presetsStore.vehicles)
})
const dateModes = computed(() => {
  return Object.values(DATE_MODES)
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
            density="compact"
            label="Battle type"
            clearable
        />
      </v-col>
      <v-col class="ml-4">
        <v-select
            v-model="selectedVehicleName"
            :items="vehicles"
            density="compact"
            label="Select vehicle"
            clearable
        />
      </v-col>
    </v-row>
    <v-row no-gutters>
      <v-col>
        <v-select
            v-model="selectedDateMode"
            :items="dateModes"
            density="compact"
            label="Date filtering mode"
            clearable
        />
      </v-col>
      <v-col class="ml-4" v-show="selectedDateMode === DATE_MODES.SPECIFIC_DATE">
        <date-picker-select name="Date" @select="onDateSelect"/>
      </v-col>
      <v-col class="ml-4" v-show="selectedDateMode === DATE_MODES.SPECIFIC_DATE_RANGE">
        <date-range-picker-select name="Dates" @select="v => selectedDateRange = v"/>
      </v-col>
    </v-row>
  </v-form>
</template>
