import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { ApplicationSettings } from '@/data/common'

export const useSettingsStore = defineStore('settings', () => {
  const settings = ref<ApplicationSettings>()

  return { settings }
})
