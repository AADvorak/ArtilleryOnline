<script setup lang="ts">
import { useBattleStore } from '~/stores/battle'
import { ref, watch } from 'vue'
import type { Battle } from '@/playground/data/battle'
import type { CommandsSender } from '@/playground/composables/commands-sender'
import { Command } from '@/playground/data/command'
import {mdiRecord, mdiStop} from '@mdi/js'

const props = defineProps<{
  commandsSender: CommandsSender
}>()

const battleStore = useBattleStore()

const csv = ref<string>('')
const active = ref<boolean>(false)

watch(
  () => battleStore.battle,
  (value) => {
    if (active.value && value) {
      appendToCsv(value)
    }
  }
)

function appendToCsv(battle: Battle) {
  csv.value += `${battle.time}`
  Object.values(battle.model.vehicles).forEach((vehicle) => {
    const state = vehicle.state
    appendNumbersToRow([
      state.position.x,
      state.position.y,
      state.position.angle,
      state.velocity.x,
      state.velocity.y,
      state.velocity.angle
    ])
  })
  csv.value += '\r\n'
}

function appendNumbersToRow(numbers: number[]) {
  numbers.forEach((number) => (csv.value += `,"${number.toFixed(3).replace('.', ',')}"`))
}

function startTracking() {
  csv.value = 'time'
  Object.values(battleStore.battle!.model.vehicles).forEach((vehicle) => {
    csv.value += `,${vehicle.id}_position_x,${vehicle.id}_position_y,${vehicle.id}_angle`
    csv.value += `,${vehicle.id}_velocity_x,${vehicle.id}_velocity_y,${vehicle.id}_velocity_angle`
  })
  csv.value += '\r\n'
  active.value = true
  props.commandsSender.sendDebugCommand({ command: Command.START_TRACKING })
}

function stopTrackingAndSaveCsv() {
  props.commandsSender.sendDebugCommand({ command: Command.STOP_TRACKING })
  active.value = false
  saveClientCsv()
  csv.value = ''
  setTimeout(getAndSaveServerCsv, 1000)
}

function saveClientCsv() {
  const href = `data:text/csv;charset=utf-8,${csv.value}`
  const fileName = 'tracking-client.csv'
  saveToFile(href, fileName)
}

function getAndSaveServerCsv() {
  const href = `/api/battles/tracking`
  const fileName = 'tracking-server.csv'
  saveToFile(href, fileName)
}

function saveToFile(href: string, fileName: string) {
  const element = document.createElement('a')
  element.setAttribute('href', href)
  element.setAttribute('download', fileName)
  element.style.display = 'none'
  document.body.appendChild(element)
  element.click()
  document.body.removeChild(element)
}
</script>

<template>
  <icon-btn
      v-if="!active"
      :icon="mdiRecord"
      tooltip="Track history"
      color="error"
      @click="startTracking"
  />
  <icon-btn
      v-else
      :icon="mdiStop"
      tooltip="Stop tracking"
      color="error"
      @click="stopTrackingAndSaveCsv"
  />
</template>
