<script setup lang="ts">
import type { StompClient } from '@/composables/stomp-client'
import { useBattleStore } from '@/stores/battle'
import { computed } from 'vue'
import { useCommandsSender } from '@/composables/commands-sender'
import { Command } from '@/data/command'
import {useUserStore} from "@/stores/user";

const props = defineProps<{
  stompClient: StompClient
}>()

const battleStore = useBattleStore()
const userStore = useUserStore()
const commandsSender = useCommandsSender(props.stompClient)

const paused = computed(() => {
  return battleStore.battle?.paused
})

function pause() {
  commandsSender.sendDebugCommand({ command: Command.PAUSE })
}

function resume() {
  commandsSender.sendDebugCommand({ command: Command.RESUME })
}

function step() {
  commandsSender.sendDebugCommand({ command: Command.STEP })
}

function switchVehicle() {
  const otherUserKey = Object.keys(battleStore.vehicles).filter(key => key !== userStore.userKey)[0]
  if (otherUserKey) {
    userStore.userKey = otherUserKey
  }
}
</script>

<template>
  <v-btn color="primary" @click="switchVehicle">Switch Vehicle</v-btn>
  <v-btn v-if="!paused" color="warning" @click="pause">Pause</v-btn>
  <v-btn v-if="paused" color="success" @click="resume">Resume</v-btn>
  <v-btn v-if="paused" color="warning" @click="step">Step</v-btn>
</template>
