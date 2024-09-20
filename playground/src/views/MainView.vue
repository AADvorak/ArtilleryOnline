<script setup lang="ts">
import BattleHeader from "@/components/BattleHeader.vue";
import BattleCanvas from "@/components/BattleCanvas.vue";
import FinishBattleDialog from "@/components/FinishBattleDialog.vue";
import {useStompClient} from "@/composables/stomp-client";

const stompClient = useStompClient()
</script>

<template>
  <v-app theme="dark">
    <BattleHeader :stomp-client="stompClient"/>
    <BattleCanvas :stomp-client="stompClient"/>
    <FinishBattleDialog />
  </v-app>
</template>
