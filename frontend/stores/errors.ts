import {ref} from 'vue'
import {defineStore} from 'pinia'
import type {ErrorResponse} from '~/data/response'
import {useStompClientStore} from "~/stores/stomp-client";
import type {StompSubscription} from "@stomp/stompjs";

export const useErrorsStore = defineStore('errors', () => {
  const errors = ref<ErrorResponse[]>([])
  const messages = ref<string[]>([])

  const stompClientStore = useStompClientStore()

  const subscriptions = ref<StompSubscription[]>([])

  function subscribe() {
    if (!subscriptions.value.length) {
      subscriptions.value.push(stompClientStore.client!.subscribe('/user/topic/errors', function (msgOut) {
        errors.value.push({status: 0, error: JSON.parse(msgOut.body)})
      }))
    }
  }

  return {errors, messages, subscribe}
})
