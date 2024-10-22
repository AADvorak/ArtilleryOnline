import { defineStore } from 'pinia'
import { ref } from 'vue'
import type {UserSetting, UserSettingsNameValueMapping} from '~/data/model'
import {DefaultControls} from '~/dictionary/default-controls'

export const useUserSettingsStore = defineStore('user-settings', () => {
  const controls = ref<UserSetting[]>()

  const controlsMapping = computed(() => {
    return toNameValueMapping(controls.value)
  })

  const controlsOrDefaults = computed(() => {
    return DefaultControls.map(control => ({
      name: control.name,
      value: controlsMapping.value[control.name] || control.value,
      description: control.description
    }))
  })

  const controlsOrDefaultsValueNameMapping = computed(() => {
    return toValueNameMapping(controlsOrDefaults.value)
  })

  function toNameValueMapping(userSettings: UserSetting[] | undefined) {
    if (!userSettings || !userSettings.length) {
      return {}
    }
    const mapping: UserSettingsNameValueMapping = {}
    userSettings.forEach(setting => mapping[setting.name] = setting.value)
    return mapping
  }

  function toValueNameMapping(userSettings: UserSetting[] | undefined) {
    if (!userSettings || !userSettings.length) {
      return {}
    }
    const mapping: UserSettingsNameValueMapping = {}
    userSettings.forEach(setting => mapping[setting.value] = setting.name)
    return mapping
  }

  return { controls, controlsOrDefaults, controlsOrDefaultsValueNameMapping }
})
