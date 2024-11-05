<script setup lang="ts">
const props = defineProps<{
  color?: string
  icon: string
  tooltip: string
}>()

const emit = defineEmits(['click'])

function click() {
  emit('click')
}
</script>

<template>
  <v-btn
      class="btn-with-icon"
      :color="props.color"
      @click="click"
  >
    <v-icon :icon="props.icon" />
    <v-tooltip
        activator="parent"
        location="bottom"
        open-delay="1000">
      {{ props.tooltip }}
    </v-tooltip>
  </v-btn>
</template>

<style scoped>
.btn-with-icon {
  width: 36px;
  min-width: 36px;
  padding: 0 0;
}
</style>
