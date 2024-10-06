<script setup lang="ts">
import type { StompClient } from '@/composables/stomp-client'
import { useBattleStore } from '@/stores/battle'
import { useCommandsSender } from '@/composables/commands-sender'
import { Command } from '@/data/command'
import {useUserStore} from "@/stores/user";
import {useSettingsStore} from "@/stores/settings";

const props = defineProps<{
  stompClient: StompClient
}>()

const battleStore = useBattleStore()
const userStore = useUserStore()
const settingsStore = useSettingsStore()
const commandsSender = useCommandsSender(props.stompClient)

function pause() {
  commandsSender.sendDebugCommand({ command: Command.PAUSE })
}

function resume() {
  commandsSender.sendDebugCommand({ command: Command.RESUME })
}

function step() {
  battleStore.doStep = true
  commandsSender.sendDebugCommand({ command: Command.STEP })
}

function switchVehicle() {
  const otherUserKey = Object.keys(battleStore.vehicles).filter(key => key !== userStore.userKey)[0]
  if (otherUserKey) {
    userStore.userKey = otherUserKey
  }
}

function switchState() {
  battleStore.showServerState = !battleStore.showServerState
}
</script>

<template>
  <v-btn color="primary" @click="switchVehicle">Switch Vehicle</v-btn>
  <v-btn v-if="!battleStore.paused" color="warning" @click="pause">Pause</v-btn>
  <v-btn v-if="battleStore.paused" color="success" @click="resume">Resume</v-btn>
  <v-btn v-if="battleStore.paused" color="warning" @click="step">Step</v-btn>
  <v-btn v-if="battleStore.paused && settingsStore.settings.clientProcessing" color="secondary" @click="switchState">
    State: {{ battleStore.showServerState ? 'server' : 'client' }}
  </v-btn>
</template>
