<script setup lang="ts">
import type { StompClient } from '@/composables/stomp-client'
import { useBattleStore } from '@/stores/battle'
import { computed } from 'vue'
import { useCommandsSender } from '@/composables/commands-sender'
import { Command } from '@/data/command'

const props = defineProps<{
  stompClient: StompClient
}>()

const battleStore = useBattleStore()
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
</script>

<template>
  <v-btn v-if="!paused" color="warning" @click="pause">Pause</v-btn>
  <v-btn v-if="paused" color="success" @click="resume">Resume</v-btn>
  <v-btn v-if="paused" color="warning" @click="step">Step</v-btn>
</template>
