<script setup lang="ts">
const props = defineProps<{
  value: number
  text: string
  color: string
}>()
</script>

<template>
  <v-progress-linear
      class="progress"
      bg-color="blue-grey"
      height="16"
      :color="props.color"
      :model-value="props.value"
  >
    <span class="progress-text">{{ props.text }}</span>
  </v-progress-linear>
</template>

<style scoped>
.progress {
  border-radius: 4px;
  overflow: hidden;
}

.progress-text {
  font-size: 14px;
  font-weight: bold;
  color: white;
  text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.8);
  padding: 0 4px;
  white-space: nowrap;
}
</style>
