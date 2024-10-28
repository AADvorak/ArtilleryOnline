import { ref } from 'vue'
import { defineStore } from 'pinia'
import {useHostStore} from '~/stores/host'
import {useCsrfStore} from '~/stores/csrf'
import {CompatClient, Stomp} from '@stomp/stompjs'

export const useStompClientStore = defineStore('stomp-client', () => {
  const hostStore = useHostStore()
  const csrfStore = useCsrfStore()
  const client = ref<CompatClient>()

  function connect() {
    return new Promise((resolve, reject) => {
      if (client.value?.connected) {
        resolve()
        return
      }
      if (!client.value) {
        const socket = new WebSocket(`ws://${hostStore.host}/api/ws/websocket`)
        client.value = Stomp.over(socket)
      }
      const headers = {}
      const csrf = csrfStore.csrf
      if (csrf) {
        headers[csrf.headerName] = csrf.token
      }
      client.value.connect(headers, () => resolve())
    })
  }

  function disconnect() {
    client.value?.disconnect()
  }

  return { client, connect, disconnect }
})
