<script setup lang="ts">
import {ref} from "vue";

const props = defineProps<{
  name: string
}>()

const emit = defineEmits(['select'])

const selectedDate = ref<Date | undefined>()

watch(selectedDate, value => {
  emit('select', value)
})
</script>

<template>
  <v-select
      v-model="selectedDate"
      density="compact"
      :label="props.name"
      clearable
  >
    <template v-slot:prepend-item>
      <v-date-picker
          v-model="selectedDate"
      />
    </template>
  </v-select>
</template>
