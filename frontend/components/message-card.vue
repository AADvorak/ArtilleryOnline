<script setup lang="ts">
import type {Message} from '~/data/model'
import {useMessageStore} from '~/stores/message'
import {ApiRequestSender} from "~/api/api-request-sender";
import {useRouter} from "#app";
import {DateUtils} from "~/utils/DateUtils";
import {useRequestErrorHandler} from "~/composables/request-error-handler";

const props = defineProps<{
  message: Message
}>()

const router = useRouter()

const messageStore = useMessageStore()

const messageTime = computed(() => {
  return DateUtils.getClientDateLocaleString(props.message.time)
})

async function deleteMessage() {
  try {
    const messageId = props.message!.id
    await new ApiRequestSender().delete(`/messages/${messageId}`)
    messageStore.removeMessageById(messageId)
  } catch (e) {
    useRequestErrorHandler().handle(e)
  }
}
</script>

<template>
  <v-card width="100%">
    <v-card-text>
      <div class="d-flex">{{ messageTime }}: {{ props.message.text }}</div>
      <div class="d-flex mt-4">
        <v-btn @click="deleteMessage">Close</v-btn>
      </div>
    </v-card-text>
  </v-card>
</template>
