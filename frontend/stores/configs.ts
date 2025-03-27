import {ref} from 'vue'
import {defineStore} from 'pinia'
import {ApiRequestSender} from '~/api/api-request-sender'
import type {UserVehicleConfig, UserVehicleConfigs} from "~/data/model";

export const useConfigsStore = defineStore('configs', () => {
  const vehicleConfigs = ref<UserVehicleConfigs>({})

  const api = new ApiRequestSender()

  async function loadVehicleConfig(name: string) {
    const loadedConfig = vehicleConfigs.value[name]
    if (loadedConfig) {
      return loadedConfig
    }
    const config = await api.getJson<UserVehicleConfig>(getUrl(name))
    vehicleConfigs.value[name] = config
    return config
  }

  async function saveVehicleConfig(name: string, config: UserVehicleConfig) {
    await api.postJson<UserVehicleConfig, undefined>(getUrl(name), config)
    vehicleConfigs.value[name] = config
  }

  function getUrl(name: string) {
    return `/user-vehicle-configs/${name}`
  }

  return {vehicleConfigs, loadVehicleConfig, saveVehicleConfig}
})
