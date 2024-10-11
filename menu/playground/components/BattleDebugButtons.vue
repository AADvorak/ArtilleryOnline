<script setup lang="ts">
import type {StompClient} from '@/playground/composables/stomp-client'
import {useBattleStore} from '@/stores/battle'
import {useCommandsSender} from '@/playground/composables/commands-sender'
import {Command} from '@/playground/data/command'
import {useSettingsStore} from "@/stores/settings";
import HistoryTracker from "@/playground/components/HistoryTracker.vue";

const props = defineProps<{
  stompClient: StompClient
}>()

const battleStore = useBattleStore()
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

function switchState() {
  battleStore.showServerState = !battleStore.showServerState
}
</script>

<template>
  <v-btn v-if="!battleStore.paused" color="warning" @click="pause">Pause</v-btn>
  <v-btn v-if="battleStore.paused" color="success" @click="resume">Resume</v-btn>
  <v-btn v-if="battleStore.paused" color="warning" @click="step">Step</v-btn>
  <v-btn v-if="battleStore.paused && settingsStore.settings.clientProcessing" color="secondary" @click="switchState">
    State: {{ battleStore.showServerState ? 'server' : 'client' }}
  </v-btn>
  <HistoryTracker :commands-sender="commandsSender"/>
</template>
