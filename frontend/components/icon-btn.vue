<script setup lang="ts">
import type {VBtn} from "vuetify/components";

enum TouchType {
  START = 'touchstart',
  END = 'touchend'
}

const props = defineProps<{
  color?: string
  icon: string
  tooltip: string
  large?: boolean
}>()

const btn = ref<InstanceType<typeof VBtn> | undefined>()

onMounted(() => startListening())

onUnmounted(() => stopListening())

const emit = defineEmits(['click', TouchType.START, TouchType.END])

function startListening() {
  const el = btn.value?.$el as HTMLElement
  if (el) {
    el.addEventListener(TouchType.START, touchStart, false)
    el.addEventListener(TouchType.END, touchEnd, false)
  }
}

function stopListening() {
  const el = btn.value?.$el as HTMLElement
  if (el) {
    el.removeEventListener(TouchType.START, touchStart)
    el.removeEventListener(TouchType.END, touchEnd)
  }
}

function click() {
  emit('click')
}

function touchStart() {
  emit(TouchType.START)
}

function touchEnd() {
  emit(TouchType.END)
}
</script>

<template>
  <v-btn
      ref="btn"
      :class="props.large ? 'btn-with-icon-large' : 'btn-with-icon'"
      variant="text"
      :color="props.color"
      @click="click"
  >
    <v-icon
        :class="props.large ? 'icon-large' : ''"
        :icon="props.icon"
    />
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

.btn-with-icon-large {
  width: 48px;
  min-width: 48px;
  padding: 0 0;
}

.icon-large {
  width: 28px;
  height: 28px;
}
</style>
