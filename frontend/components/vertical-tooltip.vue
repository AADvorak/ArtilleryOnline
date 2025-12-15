<script setup lang="ts">
import {VerticalTooltipLocation} from "~/data/model";

const WIDE_SIZE = '180px'
const NARROW_SIZE = '30px'

const props = defineProps<{
  tooltip: string
  location?: VerticalTooltipLocation
  show?: boolean
}>()

const textClass = computed(() => props.location ? `vertical-tooltip-${props.location}` : '')
</script>

<template>
  <v-tooltip
      :model-value="props.show"
      activator="parent"
      :open-on-click="false"
      :open-on-hover="false"
      :open-on-focus="false"
      :location="props.location"
      :width="props.location ? NARROW_SIZE : WIDE_SIZE"
      :height="props.location ? WIDE_SIZE : NARROW_SIZE"
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
