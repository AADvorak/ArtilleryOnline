import { ref } from 'vue'
import { defineStore } from 'pinia'
import {useCsrfStore} from '~/stores/csrf'
import {CompatClient, Stomp} from '@stomp/stompjs'

export const useStompClientStore = defineStore('stomp-client', () => {
  const csrfStore = useCsrfStore()
  const client = ref<CompatClient>()
  const connected = ref(false)

  function connect() {
    return new Promise((resolve: (value: void) => void, reject) => {
      if (client.value?.connected) {
        resolve()
        return
      }
      if (!client.value) {
        const socket = new WebSocket(`ws://${window.location.host}/api/ws/websocket`)
        client.value = Stomp.over(socket)
      }
      const headers = {}
      const csrf = csrfStore.csrf
      if (csrf) {
        headers[csrf.headerName] = csrf.token
      }
      client.value.connect(headers, () => {
        resolve()
        connected.value = true
      }, () => {}, () => {
        connected.value = false
      })
    })
  }

  function disconnect() {
    client.value?.disconnect()
  }

  return { client, connected, connect, disconnect }
})
