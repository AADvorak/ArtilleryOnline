import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { ApplicationSettings } from '@/playground/data/common'

export const useSettingsStore = defineStore('settings', () => {
  const settings = ref<ApplicationSettings>()

  return { settings }
})
