<script setup lang="ts">
import {computed, onMounted, watch} from "vue";
import {useBattleLoader} from "@/composables/battle-loader.js";
import type {VehicleModel} from "@/data/model";
import {useBattleStore} from "@/stores/battle";
import type {Position} from "@/data/common";

const battleStore = useBattleStore()
const vehicles = computed(() => battleStore.vehicles)
const battleSize = computed(() => {
  const roomSpecs = battleStore.battle.model?.room?.specs
  if (!roomSpecs) {
    return {
      width: 1,
      height: 1
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
  const width = window.innerWidth
  const height = Math.floor(battleSize.value.height * width / battleSize.value.width)
  return {width, height}
})
const scaleCoefficient = computed(() => {
  return canvasSize.value.width / battleSize.value.width
})

watch(vehicles, () => setTimeout(drawVehicles))

onMounted(() => {
  useBattleLoader().startBattleLoading()
})

function drawVehicles() {
  if (vehicles.value) {
    Object.values(vehicles.value).forEach(drawVehicle)
  }
}

function drawVehicle(vehicleModel: VehicleModel) {
  const canvas = document.getElementById('battle-canvas')
  if (canvas.getContext) {
    const ctx = canvas.getContext('2d')
    ctx.fillStyle = 'rgb(200 0 0)'
    ctx.beginPath()
    const position = transformPosition(vehicleModel.state.position)
    const gunEndPosition = transformPosition({
      x: vehicleModel.state.position.x + vehicleModel.specs.radius * 1.5 * Math.cos(vehicleModel.state.gunAngle),
      y: vehicleModel.state.position.y + vehicleModel.specs.radius * 1.5 * Math.sin(vehicleModel.state.gunAngle)
    })
    const radius = vehicleModel.specs.radius * scaleCoefficient.value
    const startAngle = Math.PI
    const endAngle = 2 * Math.PI
    ctx.arc(position.x, position.y, radius, startAngle, endAngle)
    ctx.fill()
    ctx.moveTo(position.x, position.y);
    ctx.lineTo(gunEndPosition.x, gunEndPosition.y)
    ctx.fill()
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
  <canvas id="battle-canvas" :width="canvasSize.width" :height="canvasSize.height"></canvas>
</template>

<style scoped>
canvas {
  border: 2px solid blue;
}
</style>
