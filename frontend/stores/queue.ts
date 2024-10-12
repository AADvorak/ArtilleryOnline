import {ref} from 'vue'
import {defineStore} from 'pinia'
import {ApiRequestSender} from '~/api/api-request-sender'
import type {UserBattleQueueResponse} from '~/data/response'

export const useQueueStore = defineStore('queue', () => {
  const addTime = ref<string>()

  async function loadAddTimeIfNull() {
    if (!addTime.value) {
      try {
        const response = await new ApiRequestSender()
            .getJson<UserBattleQueueResponse>('/battles/queue')
        addTime.value = response.addTime
      } catch (e) {
        console.log(e)
      }
    }
  }

  return {addTime, loadAddTimeIfNull}
})
