<script setup lang="ts">
import { useBattleStore } from '@/stores/battle'
import { ref, watch } from 'vue'
import type { Battle } from '@/data/battle'

const battleStore = useBattleStore()

const csv = ref<string>('')
const active = ref<boolean>(false)

watch(() => battleStore.battle, (value) => {
  if (active.value) {
    appendToCsv(value)
  }
})

function appendToCsv(battle: Battle) {
  csv.value += `${battle.time}`
  Object.values(battle.model.vehicles).forEach((vehicle) => {
    const state = vehicle.state
    appendNumbersToRow([
      state.position.x,
      state.position.y,
      state.angle,
      state.velocity.x,
      state.velocity.y,
      state.velocity.angle
    ])
  })
  csv.value += '\r\n'
}

function appendNumbersToRow(numbers: number[]) {
  numbers.forEach(number => csv.value += `,"${number.toFixed(3).replace('.', ',')}"`)
}

function startTracking() {
  csv.value = 'time'
  Object.values(battleStore.battle.model.vehicles).forEach((vehicle) => {
    csv.value += `,${vehicle.id}_position_x,${vehicle.id}_position_y,${vehicle.id}_angle`
    csv.value += `,${vehicle.id}_velocity_x,${vehicle.id}_velocity_y,${vehicle.id}_velocity_angle`
  })
  csv.value += '\r\n'
  active.value = true
}

function stopTrackingAndSaveCsv() {
  active.value = false
  saveToFile()
  csv.value = ''
}

function saveToFile() {
  const href = `data:text/csv;charset=utf-8,${csv.value}`
  const fileName = 'tracking-client.csv'
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
  <v-btn color="success" v-if="!active" @click="startTracking">Track history</v-btn>
  <v-btn color="error" v-else @click="stopTrackingAndSaveCsv">Stop tracking</v-btn>
</template>
