<script setup lang="ts">
import {ref} from "vue";
import { mdiDoorOpen } from '@mdi/js'
import {useI18n} from "vue-i18n";
import {ApiRequestSender} from "~/api/api-request-sender";
import {useRoute, useRouter} from "#app";
import {useRoomStore} from "~/stores/room";
import {useRequestErrorHandler} from "~/composables/request-error-handler";

const api = new ApiRequestSender()
const requestErrorHandler = useRequestErrorHandler()
const {t} = useI18n()
const roomStore = useRoomStore()
const router = useRouter()
const route = useRoute()

const opened = ref(false)

const isShow = computed(() => {
  return !!roomStore.room && !route.path.endsWith('/rooms/room')
})

async function toRoom() {
  await router.push('/rooms/room')
}

async function exit() {
  try {
    await api.delete('/rooms/my/exit')
    roomStore.room = undefined
  } catch (e) {
    requestErrorHandler.handle(e)
  }
}
</script>

<template>
  <v-menu
      v-if="isShow"
      v-model="opened"
      close-on-content-click
      location="bottom"
  >
    <template v-slot:activator="{ props }">
      <v-btn
          class="btn-with-icon"
          color="primary"
          v-bind="props"
      >
        <v-icon :icon="mdiDoorOpen" />
        <v-tooltip
            activator="parent"
            location="bottom"
            open-delay="1000">
          {{ t('room.title') }}
        </v-tooltip>
      </v-btn>
    </template>
    <v-card>
      <v-btn class="mt-2" width="100%" @click="exit">{{ t('common.exit') }}</v-btn>
      <v-btn class="mt-2" width="100%" color="secondary" @click="toRoom">
        {{ t('rooms.backToRoom') }}
      </v-btn>
    </v-card>
  </v-menu>
</template>

<style scoped>
.btn-with-icon {
  width: 36px;
  min-width: 36px;
  padding: 0 0;
}
</style>
