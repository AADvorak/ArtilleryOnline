<script setup lang="ts">
import BattleHeader from "@/playground/components/BattleHeader.vue";
import BattleCanvas from "@/playground/components/BattleCanvas.vue";
import FinishBattleDialog from "@/playground/components/FinishBattleDialog.vue";
import {useStompClient} from "@/playground/composables/stomp-client";
import {useSettingsStore} from "@/playground/stores/settings";
import {onMounted} from "vue";
import {ApiRequestSender} from "@/api/api-request-sender";
import type {ApplicationSettings} from "@/playground/data/common";

const stompClient = useStompClient()

const settingsStore = useSettingsStore()

onMounted(() => {
  loadApplicationSettings()
})

async function loadApplicationSettings() {
  settingsStore.settings = await new ApiRequestSender()
      .getJson<ApplicationSettings>('/application/settings')
}
</script>

<template>
  <BattleHeader :stomp-client="stompClient"/>
  <BattleCanvas :stomp-client="stompClient"/>
  <FinishBattleDialog />
</template>
