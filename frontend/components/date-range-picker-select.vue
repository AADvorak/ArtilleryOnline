<script setup lang="ts">
import {ref} from "vue";
import type {DateRange} from "~/data/model";

const props = defineProps<{
  name: string
}>()

const emit = defineEmits(['select'])

const menuOpened = ref(false)
const selectedDates = ref<Date[]>([])

const dateRange = computed<DateRange | undefined>(() => {
  const dates = selectedDates.value
  if (!!dates.length && dates.length > 1) {
    return {
      from: dates[0],
      to: dates[dates.length - 1]
    }
  }
})
const selectedDatesFormat = computed({
  get() {
    if (dateRange.value) {
      return dateRange.value.from.toLocaleDateString() + ' - ' + dateRange.value.to.toLocaleDateString()
    }
  },
  set(newValue) {
    if (newValue) {
      selectedDates.value = [new Date(newValue)]
    } else {
      selectedDates.value = []
    }
  }
})

watch(dateRange, value => {
  menuOpened.value = false
  emit('select', value)
})
</script>

<template>
  <v-select
      v-model="selectedDatesFormat"
      v-model:menu="menuOpened"
      density="compact"
      :label="props.name"
      clearable
  >
    <template v-slot:prepend-item>
      <v-date-picker
          v-model="selectedDates"
          hide-header
          hide-weekdays
          multiple="range"
      />
    </template>
  </v-select>
</template>
