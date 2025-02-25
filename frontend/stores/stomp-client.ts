import { ref } from 'vue'
import { defineStore } from 'pinia'
import {useCsrfStore} from '~/stores/csrf'
import {CompatClient, Stomp, type StompSubscription} from '@stomp/stompjs'

export const useStompClientStore = defineStore('stomp-client', () => {
  const csrfStore = useCsrfStore()
  const client = ref<CompatClient>()
  const connected = ref(false)

  const pongSubscription: StompSubscription | undefined = undefined
  const pingTime = ref<number>()

  function connect() {
    return new Promise((resolve: (value: void) => void, reject) => {
      if (connected.value) {
        resolve()
        return
      }
      const socket = new WebSocket(`ws://${window.location.host}/api/ws/websocket`)
      client.value = Stomp.over(socket)
      client.value.debug = () => {}
      const headers = {}
      const csrf = csrfStore.csrf
      if (csrf) {
        // @ts-ignore
        headers[csrf.headerName] = csrf.token
      }
      client.value.connect(headers, () => {
        resolve()
        connected.value = true
        subscribePong()
        sendPing()
      }, () => {}, () => {
        connected.value = false
      })
    })
  }

  function disconnect() {
    unsubscribePong()
    client.value?.disconnect()
  }

  function subscribePong() {
    client.value?.subscribe('/user/topic/pong', msgOut => {
      const response = JSON.parse(msgOut.body)
      const pingTimestamp = response.timestamp
      const pongTimestamp = new Date().getTime()
      pingTime.value = pongTimestamp - pingTimestamp
    })
  }

  function unsubscribePong() {
    if (pongSubscription) {
      pongSubscription.unsubscribe()
    }
  }

  function sendPing() {
    if (!connected.value || !client.value) {
      return
    }
    client.value.send(
        '/api/ws/ping',
        {},
        JSON.stringify({
          timestamp: new Date().getTime()
        })
    )
    setTimeout(sendPing, 10000)
  }

  return { client, connected, connect, disconnect, pingTime }
})
