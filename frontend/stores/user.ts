import {ref} from 'vue'
import {defineStore} from 'pinia'
import type {User} from '~/data/model'
import {ApiRequestSender} from '~/api/api-request-sender'

export const useUserStore = defineStore('user', () => {
  const user = ref<User | undefined>()

  async function loadUserIfNull() {
    if (!user.value) {
      user.value = await new ApiRequestSender().getJson<User>('/users/me')
    }
  }

  return {user, loadUserIfNull}
})
