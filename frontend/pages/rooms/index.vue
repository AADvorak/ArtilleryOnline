<script setup lang="ts">
import {useRouter} from "#app";
import {ApiRequestSender} from "~/api/api-request-sender";
import type {Room} from "~/data/model";
import {useRoomStore} from "~/stores/room";
import {useRequestErrorHandler} from "~/composables/request-error-handler";
import {useI18n} from "vue-i18n";
import OpenRoomsTable from "~/components/open-rooms-table.vue";
import {mdiRefresh} from "@mdi/js";

const {t} = useI18n()
const router = useRouter()

const roomStore = useRoomStore()

const openRoomsTable = ref<InstanceType<typeof OpenRoomsTable> | undefined>()

async function createRoom() {
  try {
    roomStore.room = await new ApiRequestSender().putJson<undefined, Room>('/rooms', undefined)
    await toRoom()
  } catch (e) {
    useRequestErrorHandler().handle(e)
  }
}

async function toRoom() {
  await router.push('/rooms/room')
}

function loadRooms() {
  openRoomsTable.value?.loadRooms()
}

function back() {
  router.push('/menu')
}
</script>

<template>
  <NuxtLayout>
    <v-card width="100%" max-width="600px">
      <v-card-title>
        <menu-navigation>
          <icon-btn
              :icon="mdiRefresh"
              :tooltip="t('common.refresh')"
              @click="loadRooms"
          />
        </menu-navigation>
      </v-card-title>
      <v-card-text>
        <v-btn v-if="!roomStore.room" class="mb-4" width="100%" color="secondary" @click="createRoom">
          {{ t('rooms.createRoom') }}
        </v-btn>
        <v-btn v-else class="mb-4" width="100%" color="secondary" @click="toRoom">
          {{ t('rooms.backToRoom') }}
        </v-btn>
        <open-rooms-table ref="openRoomsTable" class="mb-4"/>
        <v-btn class="mb-4" width="100%" @click="back">{{ t('common.back') }}</v-btn>
      </v-card-text>
    </v-card>
  </NuxtLayout>
</template>
