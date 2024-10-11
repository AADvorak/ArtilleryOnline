<script setup lang="ts">
import BattleHeader from "@/playground/components/BattleHeader.vue";
import BattleCanvas from "@/playground/components/BattleCanvas.vue";
import FinishBattleDialog from "@/playground/components/FinishBattleDialog.vue";
import {useStompClient} from "@/playground/composables/stomp-client";

const stompClient = useStompClient()
</script>

<template>
  <BattleHeader :stomp-client="stompClient"/>
  <BattleCanvas :stomp-client="stompClient"/>
  <FinishBattleDialog />
</template>
