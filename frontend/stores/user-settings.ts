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

  function setControl(newControl: UserSetting) {
    if (!controls.value) {
      controls.value = []
    }
    const existingControl = controls.value
        .filter(control => control.name === newControl.name)[0]
    if (existingControl) {
      existingControl.value = newControl.value
    } else {
      controls.value.push(newControl)
    }
    saveControls().then()
  }

  function resetControls() {
    controls.value = []
    saveControls().then()
  }

  async function saveControls() {
    // todo store on server
    localStorage.setItem('user-settings.controls', JSON.stringify(controls.value))
  }

  async function loadControlsIfNull() {
    // todo store on server
    const controlsStr = localStorage.getItem('user-settings.controls')
    if (controlsStr) {
      controls.value = JSON.parse(controlsStr)
    } else {
      controls.value = []
    }
  }

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

  return {
    controls,
    controlsOrDefaults,
    controlsOrDefaultsValueNameMapping,
    setControl,
    resetControls,
    loadControlsIfNull
  }
})
