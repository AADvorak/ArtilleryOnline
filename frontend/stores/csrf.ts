import {ref} from 'vue'
import {defineStore} from 'pinia'
import type {CsrfToken} from '~/data/model'

export const useCsrfStore = defineStore('csrf', () => {
  const csrf = ref<CsrfToken>()

  return {csrf}
})
