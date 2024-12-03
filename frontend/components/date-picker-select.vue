<script setup lang="ts">
import {ref} from "vue";

const props = defineProps<{
  name: string
}>()

const emit = defineEmits(['select'])

const menuOpened = ref(false)
const selectedDate = ref<Date | undefined>()

watch(selectedDate, value => {
  menuOpened.value = false
  emit('select', value)
})
</script>

<template>
  <v-select
      v-model="selectedDate"
      v-model:menu="menuOpened"
      density="compact"
      :label="props.name"
      clearable
  >
    <template v-slot:prepend-item>
      <v-date-picker
          v-model="selectedDate"
          hide-header
          hide-weekdays
      />
    </template>
  </v-select>
</template>
