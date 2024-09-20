import { CompatClient, Stomp } from '@stomp/stompjs'
import {useHostStore} from "@/stores/host";
import {type Ref, ref} from "vue";

export interface StompClient {
  client: Ref<CompatClient | undefined>
  addConnectCallback: Function
  connect: Function
  disconnect: Function
}

export function useStompClient(): StompClient {
  const connectCallbacks: Function[] = []
  const hostStore = useHostStore()
  const client = ref<CompatClient>()

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
