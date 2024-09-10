import { CompatClient, Stomp } from '@stomp/stompjs'
import { ref } from 'vue'

export function useStompClient() {
  const client = ref<CompatClient>()
  const connectCallbacks: Function[] = []

  function addConnectCallback(connectCallback: Function) {
    connectCallbacks.push(connectCallback)
  }

  function connect() {
    const socket = new WebSocket('ws://localhost:8080/api/ws/battle/websocket')
    client.value = Stomp.over(socket)
    client.value.connect({}, function () {
      connectCallbacks.forEach((connectCallback) => connectCallback())
    })
  }

  return { client, addConnectCallback, connect }
}
