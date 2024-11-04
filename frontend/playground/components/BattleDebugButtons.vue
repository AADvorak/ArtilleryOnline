<script setup lang="ts">
import {useBattleStore} from '@/stores/battle'
import {useCommandsSender} from '@/playground/composables/commands-sender'
import {Command} from '@/playground/data/command'
import {useSettingsStore} from '@/stores/settings'
import HistoryTracker from '@/playground/components/HistoryTracker.vue'
import { mdiPause, mdiPlay, mdiStepForward } from '@mdi/js'

const battleStore = useBattleStore()
const settingsStore = useSettingsStore()
const commandsSender = useCommandsSender()

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
  <v-btn v-show="!battleStore.paused" color="warning" @click="pause">
    <v-icon :icon="mdiPause" />
    <v-tooltip
        activator="parent"
        location="bottom"
        open-delay="1000">
      Pause
    </v-tooltip>
  </v-btn>
  <v-btn v-show="battleStore.paused" color="success" @click="resume">
    <v-icon :icon="mdiPlay" />
    <v-tooltip
        activator="parent"
        location="bottom"
        open-delay="1000">
      Resume
    </v-tooltip>
  </v-btn>
  <v-btn v-show="battleStore.paused" color="warning" @click="step">
    <v-icon :icon="mdiStepForward" />
    <v-tooltip
        activator="parent"
        location="bottom"
        open-delay="1000">
      Step forward
    </v-tooltip>
  </v-btn>
  <HistoryTracker :commands-sender="commandsSender"/>
  <v-btn v-show="battleStore.paused && settingsStore.settings.clientProcessing" color="secondary" @click="switchState">
    State: {{ battleStore.showServerState ? 'server' : 'client' }}
  </v-btn>
</template>
