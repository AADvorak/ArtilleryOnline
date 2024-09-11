import { CompatClient, Stomp } from '@stomp/stompjs'
import { ref } from 'vue'
import {useHostStore} from "@/stores/host";

export function useStompClient() {
  const client = ref<CompatClient>()
  const connectCallbacks: Function[] = []
  const hostStore = useHostStore()

  function addConnectCallback(connectCallback: Function) {
    connectCallbacks.push(connectCallback)
  }

  function connect() {
    const socket = new WebSocket(`ws://${hostStore.host}/api/ws/battle/websocket`)
    client.value = Stomp.over(socket)
    client.value.connect({}, function () {
      connectCallbacks.forEach((connectCallback) => connectCallback())
    })
  }

  function disconnect() {
    client.value?.disconnect()
  }

  return { client, addConnectCallback, connect, disconnect }
}
