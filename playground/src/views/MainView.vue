<script setup lang="ts">
import BattleHeader from "@/components/BattleHeader.vue";
import BattleCanvas from "@/components/BattleCanvas.vue";
import FinishBattleDialog from "@/components/FinishBattleDialog.vue";
</script>

<template>
  <v-app theme="dark">
    <BattleHeader />
    <BattleCanvas />
    <FinishBattleDialog />
  </v-app>
</template>
