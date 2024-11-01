<script setup lang="ts">
import type {Message} from '~/data/model'
import {useMessageStore} from '~/stores/message'
import {ApiRequestSender} from "~/api/api-request-sender";
import {useRouter} from "#app";

const props = defineProps<{
  message: Message
}>()

const router = useRouter()

const messageStore = useMessageStore()

async function deleteMessage() {
  try {
    const messageId = props.message!.id
    await new ApiRequestSender().delete(`/messages/${messageId}`)
    messageStore.removeMessageById(messageId)
  } catch (e) {
    console.log(e)
  }
}
</script>

<template>
  <v-card width="100%">
    <v-card-text>
      <div class="d-flex">{{ props.message.text }}</div>
      <div class="d-flex mt-4">
        <v-btn @click="deleteMessage">Close</v-btn>
      </div>
    </v-card-text>
  </v-card>
</template>
