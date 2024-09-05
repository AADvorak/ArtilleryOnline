<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue'
import type {ShellModel, VehicleModel} from '@/data/model'
import { useBattleStore } from '@/stores/battle'
import type { Position } from '@/data/common'
import {usePlayerCommandsListener} from "@/composables/commands-sender";

const battleStore = useBattleStore()
const battle = computed(() => battleStore.battle)
const vehicles = computed(() => battleStore.vehicles)
const battleSize = computed(() => {
  const roomSpecs = battleStore.battle?.model?.room?.specs
  if (!roomSpecs) {
    return {
      width: 16,
      height: 9
    }
  }
  const rightTop = roomSpecs.rightTop
  const leftBottom = roomSpecs.leftBottom
  return {
    width: rightTop.x - leftBottom.x,
    height: rightTop.y - leftBottom.y
  }
})
const canvasSize = computed(() => {
  const width = window.innerWidth - 20
  const height = Math.floor((battleSize.value.height * width) / battleSize.value.width)
  return { width, height }
})
const scaleCoefficient = computed(() => {
  return canvasSize.value.width / battleSize.value.width
})

const canvas = ref<HTMLCanvasElement>()
const ctx = ref<CanvasRenderingContext2D>()

watch(battle, () =>
  setTimeout(() => {
    clearCanvas()
    drawVehicles()
    drawShells()
  })
)

onMounted(() => {
  initCanvasAndCtx()
  usePlayerCommandsListener().startSending()
})

function initCanvasAndCtx() {
  canvas.value = document.getElementById('battle-canvas') as HTMLCanvasElement
  if (canvas.value && canvas.value.getContext) {
    ctx.value = canvas.value.getContext('2d') as CanvasRenderingContext2D
  }
}

function clearCanvas() {
  if (ctx.value) {
    ctx.value.clearRect(0, 0, canvasSize.value.width, canvasSize.value.height)
  }
}

function drawVehicles() {
  if (vehicles.value) {
    Object.values(vehicles.value).forEach(drawVehicle)
  }
}

function drawShells() {
  battle.value?.model.shells.forEach(drawShell)
}

function drawVehicle(vehicleModel: VehicleModel) {
  if (ctx.value) {
    ctx.value.fillStyle = 'rgb(200 0 0)'
    ctx.value.lineWidth = 4
    ctx.value.strokeStyle = 'rgb(200 0 0)'
    ctx.value.beginPath()
    const position = transformPosition(vehicleModel.state.position)
    const gunEndPosition = transformPosition(getGunEndPosition(vehicleModel))
    const radius = vehicleModel.specs.radius * scaleCoefficient.value
    const startAngle = Math.PI
    const endAngle = 2 * Math.PI
    ctx.value.arc(position.x, position.y, radius, startAngle, endAngle)
    ctx.value.fill()
    ctx.value.moveTo(position.x, position.y)
    ctx.value.lineTo(gunEndPosition.x, gunEndPosition.y)
    ctx.value.stroke()
    ctx.value.closePath()
  }
}

function getGunEndPosition(vehicleModel: VehicleModel) {
  const vehiclePosition = vehicleModel.state.position
  const gunAngle = vehicleModel.state.gunAngle
  const gunLength = vehicleModel.config.gun.length
  return {
    x: vehiclePosition.x + gunLength * Math.cos(gunAngle),
    y: vehiclePosition.y + gunLength * Math.sin(gunAngle)
  }
}

function drawShell(shellModel: ShellModel) {
  if (ctx.value) {
    ctx.value.fillStyle = 'rgb(200 0 0)'
    ctx.value.beginPath()
    const position = transformPosition(shellModel.state.position)
    ctx.value.arc(position.x, position.y, 2, 0, 2 * Math.PI)
    ctx.value.fill()
    ctx.value.closePath()
  }
}

function transformAngle(angle: number) {
  return angle + Math.PI
}

function transformPosition(position: Position) {
  return {
    x: Math.floor(scaleCoefficient.value * position.x),
    y: Math.floor(canvasSize.value.height - scaleCoefficient.value * position.y)
  }
}
</script>

<template>
  <v-main>
    <canvas id="battle-canvas" :width="canvasSize.width" :height="canvasSize.height"></canvas>
  </v-main>
</template>

<style scoped>
canvas {
  border: 2px solid blue;
}
</style>
