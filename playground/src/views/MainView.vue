<script setup lang="ts">
import BattleHeader from "@/components/BattleHeader.vue";
import BattleCanvas from "@/components/BattleCanvas.vue";
import FinishBattleDialog from "@/components/FinishBattleDialog.vue";
import {useStompClient} from "@/composables/stomp-client";
import {useSettingsStore} from "@/stores/settings";
import {onMounted} from "vue";
import {ApiRequestSender} from "@/api/api-request-sender";
import type {ApplicationSettings} from "@/data/common";

const stompClient = useStompClient()

const settingsStore = useSettingsStore()

onMounted(() => {
  loadApplicationSettings()
})

async function loadApplicationSettings() {
  settingsStore.settings = await new ApiRequestSender()
      .getJson<ApplicationSettings>('/application/settings', '')
}
</script>

<template>
  <v-app theme="dark">
    <BattleHeader :stomp-client="stompClient"/>
    <BattleCanvas :stomp-client="stompClient"/>
    <FinishBattleDialog />
  </v-app>
</template>
