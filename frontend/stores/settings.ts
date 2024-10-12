import { defineStore } from 'pinia'
import { ref } from 'vue'
import type {ApplicationSettings, TimeZone} from '~/playground/data/common'
import {ApiRequestSender} from "~/api/api-request-sender";

export const useSettingsStore = defineStore('settings', () => {
  const api = new ApiRequestSender()

  const settings = ref<ApplicationSettings>()
  const timeZoneOffset = ref<number>(0)

  async function loadIfNull() {
    if (!settings.value) {
      try {
        settings.value = await api.getJson<ApplicationSettings>('/application/settings')
        timeZoneOffset.value = (await api.getJson<TimeZone>('/application/timezone')).offset
      } catch (e) {
        console.log(e)
      }
    }
  }

  return { settings, timeZoneOffset, loadIfNull }
})
