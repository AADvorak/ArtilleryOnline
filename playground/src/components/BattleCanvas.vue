<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue'
import type { ShellModel, VehicleModel } from '@/data/model'
import { useBattleStore } from '@/stores/battle'
import type { Position } from '@/data/common'
import { useCommandsSender } from '@/composables/commands-sender'
import {useBattleUpdater} from "@/composables/battle-updater";

const battleStore = useBattleStore()
const battle = computed(() => battleStore.battle)
const battleSize = ref()
const canvasSize = ref()
const scaleCoefficient = ref()
const canvasWidth = computed(() => {
  return canvasSize.value ? canvasSize.value.width : window.innerWidth - 10
})
const canvasHeight = computed(() => {
  return canvasSize.value ? canvasSize.value.height : window.innerHeight - 70
})

const canvas = ref<HTMLCanvasElement>()
const ctx = ref<CanvasRenderingContext2D>()

watch(battle, (value, oldValue) => {
  if (!oldValue && value) {
    calculateBattleSize()
    calculateCanvasSize()
    calculateScaleCoefficient()
    useCommandsSender().startSending()
    useBattleUpdater().startListening()
  }
  requestAnimationFrame(() => {
    clearCanvas()
    drawVehicles()
    drawShells()
  })
})

onMounted(() => {
  initCanvasAndCtx()
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
  if (battleStore.vehicles) {
    Object.values(battleStore.vehicles).forEach(drawVehicle)
  }
}

function drawShells() {
  if (battleStore.shells) {
    Object.values(battleStore.shells).forEach(drawShell)
  }
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

function calculateBattleSize() {
  if (battleSize.value) {
    return
  }
  const roomSpecs = battleStore.battle?.model?.room?.specs
  if (roomSpecs) {
    const rightTop = roomSpecs.rightTop
    const leftBottom = roomSpecs.leftBottom
    battleSize.value = {
      width: rightTop.x - leftBottom.x,
      height: rightTop.y - leftBottom.y
    }
  }
}

function calculateCanvasSize() {
  if (canvasSize.value) {
    return
  }
  if (battleSize.value) {
    const battleWidthToHeight = battleSize.value.width / battleSize.value.height
    const screenWidth = window.innerWidth - 10
    const screenHeight = window.innerHeight - 70
    const screenWidthToHeight = screenWidth / screenHeight
    if (battleWidthToHeight > screenWidthToHeight) {
      const width = screenWidth
      const height = Math.floor(width / battleWidthToHeight)
      canvasSize.value = { width, height }
    } else {
      const height = screenHeight
      const width = Math.floor(battleWidthToHeight * height)
      canvasSize.value = { width, height }
    }
  }
}

function calculateScaleCoefficient() {
  if (scaleCoefficient.value) {
    return
  }
  if (battleSize.value && canvasSize.value) {
    scaleCoefficient.value = canvasSize.value.width / battleSize.value.width
  }
}
</script>

<template>
  <v-main>
    <canvas
        id="battle-canvas"
        :width="canvasWidth"
        :height="canvasHeight"
    ></canvas>
  </v-main>
</template>

<style scoped>
canvas {
  border: 2px solid blue;
}
</style>
