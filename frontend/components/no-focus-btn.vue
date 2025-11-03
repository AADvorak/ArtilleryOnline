<template>
  <v-btn
      ref="btn"
      v-bind="$attrs"
      v-on="listeners"
      @click="blurButton"
  >
    <slot />
  </v-btn>
</template>

<script setup lang="ts">
import { computed, getCurrentInstance, ref } from 'vue'
import { VBtn } from 'vuetify/components'

const btn = ref<InstanceType<typeof VBtn> | null>(null)
const instance = getCurrentInstance()

const attrs = instance?.attrs ?? {}

const listeners = computed(() => {
  const result: Record<string, Function | undefined> = {}
  for (const [key, value] of Object.entries(attrs)) {
    if (key.startsWith('on')) {
      result[key] = value as any
    }
  }
  return result
})

const blurButton = () => {
  try {
    const el = (btn.value as any)?.$el ?? null
    if (el && typeof el.blur === 'function') {
      el.blur()
      return
    }
    if (document.activeElement && typeof (document.activeElement as HTMLElement).blur === 'function') {
      (document.activeElement as HTMLElement).blur()
    }
  } catch {
    // Ignore
  }
}
</script>
