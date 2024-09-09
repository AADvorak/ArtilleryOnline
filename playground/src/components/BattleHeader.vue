<script setup lang="ts">
import {ref} from "vue";
import {useUserStore} from "@/stores/user";
import {useBattleLoader} from "@/composables/battle-loader";
import {ApiRequestSender} from "@/api/api-request-sender";
import {useBattleStore} from "@/stores/battle";
import ReloadingProgress from "@/components/ReloadingProgress.vue";

const userKey = ref('Client')
const userStore = useUserStore()
const battleStore = useBattleStore()
const apiRequestSender = new ApiRequestSender()

async function toBattle() {
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
        @click="toBattle"
    >
      Battle
    </v-btn>
    <div v-if="battleStore.isActive">
      <ReloadingProgress />
    </div>
  </v-app-bar>
</template>
