<script setup lang="ts">
import {computed, ref} from "vue";
import {useUserStore} from "@/stores/user";
import {useBattleLoader} from "@/composables/battle-loader";
import {ApiRequestSender} from "@/api/api-request-sender";
import {useBattleStore} from "@/stores/battle";
import ReloadingProgress from "@/components/ReloadingProgress.vue";
import HitPointsBar from "@/components/HitPointsBar.vue";
import BattleTimer from "@/components/BattleTimer.vue";

const userKey = ref('Player')
const userStore = useUserStore()
const battleStore = useBattleStore()
const apiRequestSender = new ApiRequestSender()

const userKeys = computed(() => {
  return Object.keys(battleStore.vehicles)
})

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
    <div v-if="!!battleStore.battle" style="min-width: 100px">
      <BattleTimer />
    </div>
    <div v-if="!!battleStore.battle" style="min-width: 200px">
      <HitPointsBar v-for="userKey in userKeys" :user-key="userKey" />
    </div>
    <div v-if="battleStore.isActive" class="ml-5">
      <ReloadingProgress />
    </div>
  </v-app-bar>
</template>
