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
  <icon-btn
      v-show="!battleStore.paused"
      :icon="mdiPause"
      tooltip="Pause"
      color="warning"
      @click="pause"
  />
  <icon-btn
      v-show="battleStore.paused"
      :icon="mdiPlay"
      tooltip="Resume"
      color="success"
      @click="resume"
  />
  <icon-btn
      v-show="battleStore.paused"
      :icon="mdiStepForward"
      tooltip="Step forward"
      color="warning"
      @click="step"
  />
  <HistoryTracker :commands-sender="commandsSender"/>
  <v-btn v-show="battleStore.paused && settingsStore.settings.clientProcessing" color="secondary" @click="switchState">
    State: {{ battleStore.showServerState ? 'server' : 'client' }}
  </v-btn>
</template>
