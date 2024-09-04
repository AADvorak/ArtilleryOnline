<script setup lang="ts">
import {ref} from "vue";
import {useUserStore} from "@/stores/user";
import {useBattleLoader} from "@/composables/battle-loader";

const userKey = ref()
const userStore = useUserStore()

function battle() {
  userStore.userKey = userKey.value
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
