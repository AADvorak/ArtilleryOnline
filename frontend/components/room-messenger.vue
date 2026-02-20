<script setup lang="ts">
import {computed, nextTick, ref, watch} from 'vue'
import {mdiSend} from "@mdi/js";
import {ApiRequestSender} from "~/api/api-request-sender";
import {useI18n} from "vue-i18n";
import {useValidationLocaleUtil} from "~/composables/validation-locale-util";
import {useRoomStore} from "~/stores/room";
import {useUserStore} from "~/stores/user";
import {DefaultColors} from "~/dictionary/default-colors";

interface NicknameColors {
  [nickname: string]: string
}

const {t} = useI18n()
const {localize} = useValidationLocaleUtil(t)
const api = new ApiRequestSender()

const bottomAnchor = ref<HTMLElement>()

const roomStore = useRoomStore()
const userStore = useUserStore()

const form = reactive({
  text: ''
})
const validation = reactive({
  text: []
})
const submitting = ref<boolean>(false)

const canSendMessage = computed(() => {
  return form.text.trim().length > 0
})

const nicknameColors = computed<NicknameColors>(() => {
  const colors: NicknameColors = {}
  const room = roomStore.room
  if (room) {
    for (let teamId = 0; teamId <= 1; teamId++) {
      const teamMembers = room.members[teamId]
      if (teamMembers) {
        const isUsersTeam = teamMembers.filter(teamMember => teamMember.nickname === userStore.user!.nickname).length > 0
        for (const member of teamMembers) {
          if (roomStore.isTeamMode) {
            colors[member.nickname] = isUsersTeam ? DefaultColors.ALLY_TEAM : DefaultColors.ENEMY_TEAM
          } else {
            colors[member.nickname] = member.nickname === userStore.user!.nickname ? DefaultColors.ALLY_TEAM : DefaultColors.ENEMY_TEAM
          }
        }
      }
    }
  }
  return colors
})

const getNicknameColor = (nickname: string): string => {
  return nicknameColors.value[nickname] || '#757575'
}

async function sendMessage() {
  if (!canSendMessage.value) return
  await useFormSubmit({form, validation, submitting}).submit(async () => {
    await api.postJson('/rooms/my/messages', form)
    form.text = ''
  })
}

const scrollToBottom = async () => {
  await nextTick()
  if (bottomAnchor.value) {
    bottomAnchor.value.scrollIntoView({ behavior: 'smooth' })
  }
  roomStore.newMessagesCount = 0
}

watch(() => roomStore.messages.length, () => {
  scrollToBottom()
})

watch(bottomAnchor, () => {
  scrollToBottom()
}, { immediate: true })
</script>

<template>
  <div class="room-messenger d-flex flex-column h-100">
    <div class="messages-container flex-grow-1 overflow-y-auto pa-4">
      <div v-if="!roomStore.messages.length" class="text-center mt-4">
        {{ t('messenger.noMessages') }}
      </div>
      <div
          v-for="(message, index) in roomStore.messages"
          :key="index"
          class="message-wrapper"
          :class="{ 'own-message': message.nickname === userStore.user!.nickname }"
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
    <v-form @submit.prevent>
      <v-text-field
          class="mt-4"
          v-model="form.text"
          :error="!!validation.text.length"
          :error-messages="localize(validation.text)"
          variant="outlined"
          density="compact"
          :placeholder="t('messenger.enterMessage')"
          @keyup.enter="sendMessage"
      >
        <template v-slot:append>
          <icon-btn
              color="primary"
              :icon="mdiSend"
              :tooltip="t('messenger.sendMessage')"
              :disabled="!canSendMessage || submitting"
              @click="sendMessage"
          />
        </template>
      </v-text-field>
    </v-form>
  </div>
</template>

<style scoped>
.room-messenger {
  height: 100%;
  min-height: 100px;
  max-height: 400px;
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
