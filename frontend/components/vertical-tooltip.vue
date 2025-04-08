<script setup lang="ts">
import {VerticalTooltipLocation} from "~/data/model";

const props = defineProps<{
  tooltip: string
  location?: VerticalTooltipLocation
  show?: boolean
}>()

const location = computed(() => props.location || VerticalTooltipLocation.TOP)

const textClass = computed(() => `vertical-tooltip-${location.value}`)
</script>

<template>
  <v-tooltip
      :model-value="props.show"
      activator="parent"
      :location="location"
      width="30px"
      height="180px"
  >
    <div :class="textClass">{{ props.tooltip }}</div>
  </v-tooltip>
</template>

<style scoped>
.vertical-tooltip-top {
  transform: rotate(-90deg) translateX(-150px);
  white-space: nowrap;
}

.vertical-tooltip-bottom {
  transform: rotate(90deg);
  white-space: nowrap;
}
</style>
