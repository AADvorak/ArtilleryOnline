import {ref} from 'vue'
import {defineStore} from 'pinia'
import type {CsrfToken} from '~/data/model'
import {ApiRequestSender} from '~/api/api-request-sender'

export const useCsrfStore = defineStore('csrf', () => {
  const csrf = ref<CsrfToken>()

  async function loadCsrfIfNull() {
    if (!csrf.value) {
      try {
        csrf.value = await new ApiRequestSender().getJson<CsrfToken>('/csrf')
      } catch (e) {
        console.log(e)
      }
    }
  }

  return {csrf, loadCsrfIfNull}
})
