<script setup lang="ts">
const props = defineProps<{
  value: number
  text: string
  color: string
  clickable?: boolean
}>()

const emit = defineEmits(['click'])

const progressClass = computed(() => {
  return `progress ${props.clickable ? 'progress-clickable' : ''}`
})
</script>

<template>
  <v-progress-linear
      :class="progressClass"
      bg-color="blue-grey"
      height="16"
      :color="props.color"
      :model-value="props.value"
      @click="emit('click')"
  >
    <span class="progress-text">{{ props.text }}</span>
    <slot/>
  </v-progress-linear>
</template>

<style scoped>
.progress {
  border-radius: 4px;
  overflow: hidden;
}

.progress-clickable {
  cursor: pointer;
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
