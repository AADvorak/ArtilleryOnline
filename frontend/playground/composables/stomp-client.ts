import { CompatClient, Stomp } from '@stomp/stompjs'
import {useHostStore} from "~/stores/host";
import {type Ref, ref} from "vue";
import {useCsrfStore} from "~/stores/csrf";

export interface StompClient {
  client: Ref<CompatClient | undefined>
  addConnectCallback: Function
  connect: Function
  disconnect: Function
}

export function useStompClient(): StompClient {
  const connectCallbacks: Function[] = []
  const hostStore = useHostStore()
  const csrfStore = useCsrfStore()
  const client = ref<CompatClient>()

  function addConnectCallback(connectCallback: Function) {
    connectCallbacks.push(connectCallback)
  }

  function connect() {
    const headers = {}
    const csrf = csrfStore.csrf
    if (csrf) {
      headers[csrf.headerName] = csrf.token
    }
    const socket = new WebSocket(`ws://${hostStore.host}/api/ws/battle/websocket`)
    client.value = Stomp.over(socket)
    client.value.connect(headers, function () {
      connectCallbacks.forEach((connectCallback) => connectCallback())
    })
  }

  function disconnect() {
    client.value?.disconnect()
  }

  return { client, addConnectCallback, connect, disconnect }
}
