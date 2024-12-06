import {defineStore} from 'pinia'
import {ref} from 'vue'
import {
  type UserSetting,
  UserSettingsGroup,
  type UserSettingsGroupsMap,
  type UserSettingsNameValueMapping
} from '~/data/model'
import {DefaultControls} from '~/dictionary/default-controls'
import {ApiRequestSender} from '~/api/api-request-sender'
import {useRequestErrorHandler} from '~/composables/request-error-handler'
import {DefaultAppearances} from '~/dictionary/default-appearances'
import type {ErrorResponse} from '~/data/response'
import {DefaultSoundSettings} from "~/dictionary/default-sound-settings";

const SETTINGS_PATH = '/user-settings'

export const useUserSettingsStore = defineStore('user-settings', () => {
  const api = new ApiRequestSender()

  const settings = ref<UserSettingsGroupsMap>()

  const controls = computed(() => {
    if (!settings.value) {
      return []
    }
    return settings.value[UserSettingsGroup.CONTROLS]
  })

  const appearances = computed(() => {
    if (!settings.value) {
      return []
    }
    return settings.value[UserSettingsGroup.APPEARANCES]
  })

  const soundSettings = computed(() => {
    if (!settings.value) {
      return []
    }
    return settings.value[UserSettingsGroup.SOUNDS]
  })

  const controlsMapping = computed(() => {
    return toNameValueMapping(controls.value)
  })

  const appearancesMapping = computed(() => {
    return toNameValueMapping(appearances.value)
  })

  const soundSettingsMapping = computed(() => {
    return toNameValueMapping(soundSettings.value)
  })

  const controlsOrDefaults = computed(() => {
    return DefaultControls.map(control => ({
      name: control.name,
      value: controlsMapping.value[control.name] || control.value,
      description: control.description
    }))
  })

  const appearancesOrDefaults = computed(() => {
    return DefaultAppearances.map(appearance => ({
      name: appearance.name,
      value: appearancesMapping.value[appearance.name] || appearance.value,
      description: appearance.description
    }))
  })

  const soundSettingsOrDefaults = computed(() => {
    return DefaultSoundSettings.map(soundSetting => ({
      name: soundSetting.name,
      value: soundSettingsMapping.value[soundSetting.name] || soundSetting.value,
      description: soundSetting.description
    }))
  })

  const controlsOrDefaultsValueNameMapping = computed(() => {
    return toValueNameMapping(controlsOrDefaults.value)
  })

  const appearancesOrDefaultsNameValueMapping = computed(() => {
    return toNameValueMapping(appearancesOrDefaults.value)
  })

  const soundSettingsOrDefaultsNameValueMapping = computed(() => {
    return toNameValueMapping(soundSettingsOrDefaults.value)
  })

  async function setControl(newControl: UserSetting) {
    await setSetting(newControl, UserSettingsGroup.CONTROLS)
  }

  async function setAppearance(newAppearance: UserSetting) {
    await setSetting(newAppearance, UserSettingsGroup.APPEARANCES)
  }

  async function setSoundSetting(newSoundSetting: UserSetting) {
    await setSetting(newSoundSetting, UserSettingsGroup.SOUNDS)
  }

  async function setSetting(newSetting: UserSetting, group: UserSettingsGroup) {
    try {
      await api.putJson<UserSetting, void>(`${SETTINGS_PATH}/${group}`, newSetting)
      const existingSetting = settings.value && settings.value[group]
          ? settings.value![group].filter(setting => setting.name === newSetting.name)[0]
          : undefined
      if (existingSetting) {
        existingSetting.value = newSetting.value
      } else {
        if (!settings.value) {
          settings.value = {}
        }
        if (!settings.value[group]) {
          settings.value[group] = []
        }
        settings.value[group].push(newSetting)
      }
    } catch (e) {
      useRequestErrorHandler().handle(e as ErrorResponse)
    }
  }

  async function resetControls() {
    try {
      await api.delete(`${SETTINGS_PATH}/${UserSettingsGroup.CONTROLS}`)
      if (settings.value) {
        settings.value[UserSettingsGroup.CONTROLS] = []
      }
    } catch (e) {
      useRequestErrorHandler().handle(e as ErrorResponse)
    }
  }

  async function loadSettingsIfNull() {
    if (!settings.value) {
      try {
        settings.value = await api.getJson<UserSettingsGroupsMap>(SETTINGS_PATH)
      } catch (e) {
        console.log(e)
        settings.value = {}
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
    appearancesMapping,
    appearancesOrDefaults,
    appearancesOrDefaultsNameValueMapping,
    soundSettingsOrDefaultsNameValueMapping,
    setControl,
    setAppearance,
    setSoundSetting,
    resetControls,
    loadSettingsIfNull
  }
})
