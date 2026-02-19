<script setup lang="ts">
import { ref, computed, watch, nextTick } from 'vue'
import {mdiSend} from "@mdi/js";
import type {ChatMessage} from "~/data/model";
import {ApiRequestSender} from "~/api/api-request-sender";
import {useRequestErrorHandler} from "~/composables/request-error-handler";
import {useI18n} from "vue-i18n";

interface NicknameColors {
  [nickname: string]: string
}

interface Props {
  modelValue: ChatMessage[]
  userNickname: string
  nicknameColors: NicknameColors
}

const {t} = useI18n()
const api = new ApiRequestSender()
const requestErrorHandler = useRequestErrorHandler()

const props = defineProps<Props>()

const newMessage = ref('')
const bottomAnchor = ref<HTMLElement>()

const canSendMessage = computed(() => {
  return newMessage.value.trim().length > 0
})

const getNicknameColor = (nickname: string): string => {
  return props.nicknameColors[nickname] || '#757575' // Серый цвет по умолчанию
}

async function sendMessage() {
  if (!canSendMessage.value) return
  try {
    await api.postJson('/rooms/my/messages', {
      text: newMessage.value
    })
    newMessage.value = ''
  } catch (e) {
    requestErrorHandler.handle(e)
  }
}

const scrollToBottom = async () => {
  await nextTick()
  if (bottomAnchor.value) {
    bottomAnchor.value.scrollIntoView({ behavior: 'smooth' })
  }
}

watch(() => props.modelValue.length, () => {
  scrollToBottom()
})

watch(bottomAnchor, () => {
  scrollToBottom()
}, { immediate: true })
</script>

<template>
  <div class="room-messenger d-flex flex-column h-100">
    <div class="messages-container flex-grow-1 overflow-y-auto pa-4">
      <div v-if="!modelValue?.length" class="text-center mt-4">
        {{ t('messenger.noMessages') }}
      </div>
      <div
          v-for="(message, index) in modelValue"
          :key="index"
          class="message-wrapper"
          :class="{ 'own-message': message.nickname === userNickname }"
      >
        <div class="message-bubble">
          <div class="message-header d-flex align-center">
            <span
                class="nickname font-weight-medium mr-2"
                :style="{ color: getNicknameColor(message.nickname) }"
            >
              {{ message.nickname }}
            </span>
            <span class="time text-caption text-grey">
              {{ DateUtils.getClientDateLocaleTimeString(message.time) }}
            </span>
          </div>
          <div class="message-text">
            {{ message.text }}
          </div>
        </div>
      </div>
      <div ref="bottomAnchor" />
    </div>
    <v-divider />
    <v-text-field
        class="mt-4"
        v-model="newMessage"
        variant="outlined"
        density="compact"
        :placeholder="t('messenger.enterMessage')"
        hide-details
        @keyup.enter="sendMessage"
    >
      <template v-slot:append>
        <icon-btn
            color="primary"
            :icon="mdiSend"
            :tooltip="t('messenger.sendMessage')"
            :disabled="!canSendMessage"
            @click="sendMessage"
        />
      </template>
    </v-text-field>
  </div>
</template>

<style scoped>
.room-messenger {
  height: 100%;
  min-height: 300px;
  max-height: 300px;
}

.messages-container {
  display: flex;
  flex-direction: column;
}

.message-wrapper {
  display: flex;
  width: 100%;
}

.message-wrapper.own-message {
  justify-content: flex-end;
}

.message-bubble {
  max-width: 70%;
  padding: 4px 4px;
  word-wrap: break-word;
}

.message-header {
  margin-bottom: 4px;
  line-height: 1.2;
}

.nickname {
  font-weight: 600;
}

.time {
  opacity: 0.7;
}

.message-text {
  line-height: 1.4;
  white-space: pre-wrap;
}

.messages-container::-webkit-scrollbar {
  width: 8px;
}
</style>
