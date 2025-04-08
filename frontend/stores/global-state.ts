import {ref} from 'vue'
import {defineStore} from 'pinia'
import type {VerticalTooltipLocation} from "~/data/model";

export const useGlobalStateStore = defineStore('global-state', () => {
  const showHelp = ref<VerticalTooltipLocation | undefined>()

  return {showHelp}
})
