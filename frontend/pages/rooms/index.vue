<script setup lang="ts">
import {useRouter} from "#app";
import {ApiRequestSender} from "~/api/api-request-sender";
import type {Room} from "~/data/model";
import {useRoomStore} from "~/stores/room";
import {useRequestErrorHandler} from "~/composables/request-error-handler";

const router = useRouter()

const roomStore = useRoomStore()

async function createRoom() {
  try {
    roomStore.room = await new ApiRequestSender().putJson<undefined, Room>('/rooms', undefined)
    await router.push('/rooms/room')
  } catch (e) {
    useRequestErrorHandler().handle(e)
  }
}

function back() {
  router.push('/menu')
}
</script>

<template>
  <NuxtLayout>
    <v-card width="100%" max-width="600px">
      <v-card-title>
        Artillery online: rooms
      </v-card-title>
      <v-card-text>
        <v-btn class="mb-4" width="100%" color="secondary" @click="createRoom">Create room</v-btn>
        <v-btn class="mb-4" width="100%" @click="back">Back</v-btn>
      </v-card-text>
    </v-card>
  </NuxtLayout>
</template>
