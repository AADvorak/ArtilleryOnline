import {ref} from 'vue'
import {defineStore} from 'pinia'
import {ApiRequestSender} from '~/api/api-request-sender'
import type {UserVehicleConfig, UserVehicleConfigs} from "~/data/model";
import type {VehicleSpecs} from "~/playground/data/specs";

export const useConfigsStore = defineStore('configs', () => {
  const vehicleConfigs = ref<UserVehicleConfigs>({})

  const api = new ApiRequestSender()

  async function loadVehicleConfig(vehicleSpecs: VehicleSpecs): Promise<UserVehicleConfig> {
    let config = vehicleConfigs.value[vehicleSpecs.name]
    if (!config) {
      config = await api.getJson<UserVehicleConfig>(getUrl(vehicleSpecs.name))
      if (!config.gun) {
        config.gun = vehicleSpecs.defaultGun
      }
      vehicleConfigs.value[vehicleSpecs.name] = config
    }
    return JSON.parse(JSON.stringify(config))
  }

  async function saveVehicleConfig(name: string, config: UserVehicleConfig) {
    await api.postJson<UserVehicleConfig, undefined>(getUrl(name), config)
    vehicleConfigs.value[name] = JSON.parse(JSON.stringify(config))
  }

  function getUrl(name: string) {
    return `/user-vehicle-configs/${name}`
  }

  function clear() {
    vehicleConfigs.value = {}
  }

  return {vehicleConfigs, loadVehicleConfig, saveVehicleConfig, clear}
})
