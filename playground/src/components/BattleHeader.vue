<script setup lang="ts">
import {ref} from "vue";
import {useUserStore} from "@/stores/user";
import {useBattleLoader} from "@/composables/battle-loader";
import {ApiRequestSender} from "@/api/api-request-sender";

const userKey = ref()
const userStore = useUserStore()
const apiRequestSender = new ApiRequestSender()

async function battle() {
  userStore.userKey = userKey.value
  await apiRequestSender.putJson<undefined, void>('/battles/queue', userStore.userKey as string, undefined)
  useBattleLoader().startBattleLoading()
}
</script>

<template>
  <v-app-bar>
    <v-text-field
        v-model="userKey"
        label="Nickname"
        style="max-width: 300px"
        type="text"
        :disabled="!!userStore.userKey"
    />
    <v-btn
        color="error"
        :disabled="!!userStore.userKey || !userKey"
        @click="battle"
    >
      Battle
    </v-btn>
  </v-app-bar>
</template>
