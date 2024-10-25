import {ref} from 'vue'
import {defineStore} from 'pinia'
import {ApiRequestSender} from '~/api/api-request-sender'
import type {VehicleSpecsResponse} from '~/data/response'

export const usePresetsStore = defineStore('presets', () => {
  const vehicles = ref<VehicleSpecsResponse>()

  async function loadVehiclesIfNull() {
    if (!vehicles.value) {
      try {
        vehicles.value = await new ApiRequestSender()
            .getJson<VehicleSpecsResponse>('/presets/vehicles')
      } catch (e) {
        console.log(e)
      }
    }
  }

  return {vehicles, loadVehiclesIfNull}
})
