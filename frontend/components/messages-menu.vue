<script setup lang="ts">
import {ref} from "vue";
import {useMessageStore} from "~/stores/message";
import MessageCard from "~/components/message-card.vue";
import { mdiMessage } from '@mdi/js'
import {useI18n} from "vue-i18n";

const {t} = useI18n()
const messageStore = useMessageStore()

const opened = ref(false)

const messages = computed(() => {
  return messageStore.messages || []
})
const roomInvitations = computed(() => {
  return messageStore.roomInvitations || []
})
const count = computed(() => {
  return roomInvitations.value.length + messages.value.length
})
</script>

<template>
  <v-menu
      v-model="opened"
      close-on-content-click
      location="bottom"
  >
    <template v-slot:activator="{ props }">
      <v-btn
          class="btn-with-icon"
          :color="!!roomInvitations.length || !!messages.length ? 'primary' : ''"
          v-bind="props"
      >
        <v-badge v-if="!!count" color="error" :content="count">
          <v-icon :icon="mdiMessage" />
        </v-badge>
        <v-icon v-else :icon="mdiMessage" />
        <v-tooltip
            activator="parent"
            location="bottom"
            open-delay="1000">
          {{ t('messagesMenu.messages') }}
        </v-tooltip>
      </v-btn>
    </template>
    <v-card min-width="300" max-width="400">
      <div class="no-messages-div" v-if="!roomInvitations.length && !messages.length">
        {{ t('messagesMenu.noMessages') }}
      </div>
      <v-list v-else>
        <v-list-item v-for="invitation in roomInvitations">
          <room-invitation-card :invitation="invitation" />
        </v-list-item>
        <v-list-item v-for="message in messages">
          <message-card :message="message" />
        </v-list-item>
      </v-list>
    </v-card>
  </v-menu>
</template>

<style scoped>
.no-messages-div {
  height: 50px;
}

.btn-with-icon {
  width: 36px;
  min-width: 36px;
  padding: 0 0;
}
</style>
