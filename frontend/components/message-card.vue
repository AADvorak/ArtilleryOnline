<script setup lang="ts">
import type {Message} from '~/data/model'
import {useMessageStore} from '~/stores/message'
import {ApiRequestSender} from "~/api/api-request-sender";
import {useRouter} from "#app";
import {DateUtils} from "~/utils/DateUtils";
import {useRequestErrorHandler} from "~/composables/request-error-handler";
import {useI18n} from "vue-i18n";

const props = defineProps<{
  message: Message
}>()

const {t} = useI18n()
const router = useRouter()

const messageStore = useMessageStore()

const messageTime = computed(() => {
  return DateUtils.getClientDateLocaleString(props.message.time)
})
const messageText = computed(() => {
  const locale = props.message.locale
  if (locale) {
    return t('serverMessages.' + locale.code, locale.params)
  } else {
    return props.message.text
  }
})

async function deleteMessage() {
  const messageId = props.message!.id
  try {
    await new ApiRequestSender().delete(`/messages/${messageId}`)
  } catch (e) {
    useRequestErrorHandler().handle(e)
  } finally {
    messageStore.removeMessageById(messageId)
  }
}
</script>

<template>
  <v-card width="100%">
    <v-card-text>
      <div class="d-flex">{{ messageTime }}: {{ messageText }}</div>
      <div class="d-flex mt-4">
        <v-btn @click="deleteMessage">{{ t('common.close') }}</v-btn>
      </div>
    </v-card-text>
  </v-card>
</template>
