import { defineStore } from 'pinia'
import { ref } from 'vue'
import type {ApplicationLimits, ApplicationSettings, TimeZone} from '~/playground/data/common'
import {ApiRequestSender} from "~/api/api-request-sender";

export const useSettingsStore = defineStore('settings', () => {
  const api = new ApiRequestSender()

  const settings = ref<ApplicationSettings>()
  const limits = ref<ApplicationLimits>()
  const timeZoneOffset = ref<number | undefined>()

  async function loadIfNull() {
    if (!settings.value) {
      settings.value = await api.getJson<ApplicationSettings>('/application/settings')
    }
    if (!limits.value) {
      limits.value = await api.getJson<ApplicationLimits>('/application/limits')
    }
    if (!timeZoneOffset.value) {
      timeZoneOffset.value = (await api.getJson<TimeZone>('/application/timezone')).offset
    }
  }

  return { settings, limits, timeZoneOffset, loadIfNull }
})
