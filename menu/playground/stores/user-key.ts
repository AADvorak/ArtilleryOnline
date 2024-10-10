import { ref } from 'vue'
import { defineStore } from 'pinia'

export const useUserKeyStore = defineStore('userKey', () => {
  const userKey = ref<string>()

  return { userKey }
})
