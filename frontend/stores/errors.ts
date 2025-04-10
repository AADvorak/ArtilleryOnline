import {ref} from 'vue'
import {defineStore} from 'pinia'
import type {ErrorResponse} from '~/data/response'

export const useErrorsStore = defineStore('errors', () => {
  const errors = ref<ErrorResponse[]>([])
  const messages = ref<string[]>([])

  return {errors, messages}
})
