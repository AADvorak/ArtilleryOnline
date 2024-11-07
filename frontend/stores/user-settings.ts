import { defineStore } from 'pinia'
import { ref } from 'vue'
import type {UserSetting, UserSettingsNameValueMapping} from '~/data/model'
import {DefaultControls} from '~/dictionary/default-controls'
import {ApiRequestSender} from '~/api/api-request-sender'
import {useRequestErrorHandler} from '~/composables/request-error-handler'

const CONTROLS_PATH = '/user-settings/controls'

export const useUserSettingsStore = defineStore('user-settings', () => {
  const controls = ref<UserSetting[]>()

  const api = new ApiRequestSender()

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

  async function setControl(newControl: UserSetting) {
    try {
      await api.putJson<UserSetting, void>(CONTROLS_PATH, newControl)
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
    } catch (e) {
      useRequestErrorHandler().handle(e)
    }
  }

  async function resetControls() {
    try {
      await api.delete(CONTROLS_PATH)
      controls.value = []
    } catch (e) {
      useRequestErrorHandler().handle(e)
    }
  }

  async function loadControlsIfNull() {
    if (!controls.value) {
      try {
        controls.value = await api.getJson<UserSetting[]>(CONTROLS_PATH)
      } catch (e) {
        console.log(e)
        controls.value = []
      }
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
