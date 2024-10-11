<script setup lang="ts">
import {useRouter} from "#app";
import {useBattleLoader} from "~/playground/composables/battle-loader";
import {ApiRequestSender} from "~/api/api-request-sender";
import {useBattleStore} from "~/stores/battle";

const router = useRouter()
const battleStore = useBattleStore()

const waitingForBattle = ref<boolean>(false)

watch(() => battleStore.battle, (value) => {
  if (value) {
    setTimeout(() => router.push('/playground'))
  }
})

async function randomBattle() {
  try {
    await new ApiRequestSender().putJson<undefined, void>('/battles/queue', undefined)
    waitingForBattle.value = true
    useBattleLoader().startBattleLoading()
  } catch (e) {
    console.log(e)
  }
}

function back() {
  router.push('/menu')
}
</script>

<template>
  <v-card width="100%" max-width="600px">
    <v-card-title>
      Artillery online: battle
    </v-card-title>
    <v-card-text>
      <v-btn class="mb-4" width="100%" color="error" :loading="waitingForBattle" @click="randomBattle">Random battle</v-btn>
      <v-btn class="mb-4" width="100%" @click="back">Back</v-btn>
    </v-card-text>
  </v-card>
</template>
