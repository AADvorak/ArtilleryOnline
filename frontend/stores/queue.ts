import {ref} from 'vue'
import {defineStore} from 'pinia'
import {ApiRequestSender} from '~/api/api-request-sender'
import type {UserBattleQueueResponse} from '~/data/response'

export const useQueueStore = defineStore('queue', () => {
  const queue = ref<UserBattleQueueResponse | undefined>()

  async function loadQueueIfNull() {
    if (!queue.value) {
      try {
        queue.value = await new ApiRequestSender()
            .getJson<UserBattleQueueResponse>('/battles/queue')
      } catch (e) {
        console.log(e)
      }
    }
  }

  return {queue, loadQueueIfNull}
})
