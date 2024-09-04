<script setup lang="ts">
import {computed, onMounted, ref, watch} from "vue";
import type {VehicleModel} from "@/data/model";
import {useBattleStore} from "@/stores/battle";
import type {Position} from "@/data/common";

const battleStore = useBattleStore()
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
  const height = Math.floor(battleSize.value.height * width / battleSize.value.width)
  return {width, height}
})
const scaleCoefficient = computed(() => {
  return canvasSize.value.width / battleSize.value.width
})

const canvas = ref<HTMLCanvasElement>()
const ctx = ref<CanvasRenderingContext2D>()

watch(vehicles, () => setTimeout(drawVehicles))

onMounted(() => {
  initCanvasAndCtx()
})

function initCanvasAndCtx() {
  canvas.value = document.getElementById('battle-canvas') as HTMLCanvasElement
  if (canvas.value && canvas.value.getContext) {
    ctx.value = canvas.value.getContext('2d') as CanvasRenderingContext2D
  }
}

function drawVehicles() {
  if (vehicles.value) {
    Object.values(vehicles.value).forEach(drawVehicle)
  }
}

function drawVehicle(vehicleModel: VehicleModel) {
  if (ctx.value) {
    ctx.value.fillStyle = 'rgb(200 0 0)'
    ctx.value.lineWidth = 4
    ctx.value.strokeStyle = 'rgb(200 0 0)'
    ctx.value.beginPath()
    const position = transformPosition(vehicleModel.state.position)
    const gunEndPosition = transformPosition({
      x: vehicleModel.state.position.x + vehicleModel.specs.radius * 1.5 * Math.cos(vehicleModel.state.gunAngle),
      y: vehicleModel.state.position.y + vehicleModel.specs.radius * 1.5 * Math.sin(vehicleModel.state.gunAngle)
    })
    const radius = vehicleModel.specs.radius * scaleCoefficient.value
    const startAngle = Math.PI
    const endAngle = 2 * Math.PI
    ctx.value.arc(position.x, position.y, radius, startAngle, endAngle)
    ctx.value.fill()
    ctx.value.moveTo(position.x, position.y);
    ctx.value.lineTo(gunEndPosition.x, gunEndPosition.y)
    ctx.value.stroke()
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
