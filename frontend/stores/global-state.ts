import {ref} from 'vue'
import {defineStore} from 'pinia'

export const useGlobalStateStore = defineStore('global-state', () => {
  const showHelp = ref<boolean>(false)

  return {showHelp}
})
