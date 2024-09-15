import { ref } from 'vue'
import { defineStore } from 'pinia'

export const useHostStore = defineStore('host', () => {
  const host = ref<string>(window.location.host)

  return { host }
})
