<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue'
import type {RoomModel, ShellModel, VehicleModel} from '@/data/model'
import { useBattleStore } from '@/stores/battle'
import type { Position } from '@/data/common'
import { useCommandsSender } from '@/composables/commands-sender'
import {useBattleUpdater} from "@/composables/battle-updater";
import {useUserStore} from "@/stores/user";
import {useStompClient} from "@/composables/stomp-client";
import {useBattleProcessor} from "@/processor/battle-processor";

const battleStore = useBattleStore()
const userStore = useUserStore()
const stompClient = useStompClient()
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
    startBattle()
  }
  if (oldValue && !value) {
    finishBattle()
    return
  }
  redrawBattle()
})

onMounted(() => {
  initCanvasAndCtx()
  useBattleUpdater(stompClient).subscribeAfterWsConnect()
  useCommandsSender(stompClient).startSending()
})

function startBattle() {
  calculateBattleSize()
  calculateCanvasSize()
  calculateScaleCoefficient()
  stompClient.connect()
  //useBattleProcessor().startProcessing()
}

function finishBattle() {
  clearCanvas()
  battleSize.value = undefined
  canvasSize.value = undefined
  scaleCoefficient.value = undefined
  stompClient.disconnect()
}

function initCanvasAndCtx() {
  canvas.value = document.getElementById('battle-canvas') as HTMLCanvasElement
  if (canvas.value && canvas.value.getContext) {
    ctx.value = canvas.value.getContext('2d') as CanvasRenderingContext2D
  }
}

function redrawBattle() {
  requestAnimationFrame(() => {
    clearCanvas()
    drawShells()
    drawGround()
    drawVehicles()
  })
}

function clearCanvas() {
  if (ctx.value) {
    ctx.value.clearRect(0, 0, canvasSize.value.width, canvasSize.value.height)
  }
}

function drawGround() {
  const roomModel = battle.value?.model.room
  if (ctx.value && roomModel) {
    ctx.value.fillStyle = 'rgb(100 100 100)'
    ctx.value.strokeStyle = 'rgb(100 100 100)'
    ctx.value.lineWidth = 1
    let position = getGroundPosition(0, roomModel)
    ctx.value.moveTo(position.x, position.y)
    for (let i = 1; i < roomModel.state.groundLine.length; i++) {
      position = getGroundPosition(i, roomModel)
      ctx.value.lineTo(position.x, position.y)
    }
    ctx.value.stroke()
    ctx.value.closePath()
  }
}

function getGroundPosition(i: number, roomModel: RoomModel) {
  return transformPosition({
    x: roomModel.specs.step * i,
    y: roomModel.state.groundLine[i]
  })
}

function drawVehicles() {
  if (battleStore.vehicles) {
    Object.keys(battleStore.vehicles).forEach(drawVehicle)
  }
}

function drawShells() {
  if (battleStore.shells) {
    Object.values(battleStore.shells).forEach(drawShell)
  }
}

function drawVehicle(userKey: string) {
  const vehicleModel = battleStore.vehicles[userKey]
  const color = getColor(userKey)
  if (ctx.value) {
    ctx.value.fillStyle = color
    ctx.value.strokeStyle = color
    ctx.value.beginPath()
    const position = transformPosition(vehicleModel.state.position)
    const gunEndPosition = transformPosition(getGunEndPosition(vehicleModel))
    const radius = vehicleModel.specs.radius * scaleCoefficient.value
    const startAngle = Math.PI - vehicleModel.state.angle
    const endAngle = 2 * Math.PI - vehicleModel.state.angle
    ctx.value.arc(position.x, position.y, radius, startAngle, endAngle)
    ctx.value.fill()
    ctx.value.lineWidth = 4
    ctx.value.moveTo(position.x, position.y)
    ctx.value.lineTo(gunEndPosition.x, gunEndPosition.y)
    ctx.value.stroke()
    ctx.value.closePath()
    ctx.value.lineWidth = 1
  }
}

function getColor(userKey: string) {
  return userKey === userStore.userKey ? 'rgb(60,200,0)' : 'rgb(200 0 0)'
}

function getGunEndPosition(vehicleModel: VehicleModel) {
  const vehiclePosition = vehicleModel.state.position
  const gunAngle = vehicleModel.state.gunAngle
  const angle = vehicleModel.state.angle
  const gunLength = vehicleModel.config.gun.length
  return {
    x: vehiclePosition.x + gunLength * Math.cos(gunAngle + angle),
    y: vehiclePosition.y + gunLength * Math.sin(gunAngle + angle)
  }
}

function drawShell(shellModel: ShellModel) {
  if (ctx.value) {
    ctx.value.fillStyle = 'rgb(256 256 256)'
    ctx.value.lineWidth = 1
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
