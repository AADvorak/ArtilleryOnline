<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue'
import { useBattleStore } from '@/stores/battle'
import { useCommandsSender } from '@/composables/commands-sender'
import {useBattleUpdater} from "@/composables/battle-updater";
import {type StompClient} from "@/composables/stomp-client";
import {useBattleProcessor} from "@/processor/battle-processor";
import {useDrawerBase} from "@/composables/drawer/drawer-base";
import {useVehicleDrawer} from "@/composables/drawer/vehicle-drawer";
import {useShellDrawer} from "@/composables/drawer/shell-drawer";
import {useExplosionDrawer} from "@/composables/drawer/explosion-drawer";
import {useGroundDrawer} from "@/composables/drawer/ground-drawer";

const props = defineProps<{
  stompClient: StompClient
}>()

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

const drawerBase = useDrawerBase(scaleCoefficient, canvasSize)
const vehicleDrawer = useVehicleDrawer(drawerBase, ctx)
const shellDrawer = useShellDrawer(drawerBase, ctx)
const explosionDrawer = useExplosionDrawer(drawerBase, ctx)
const groundDrawer = useGroundDrawer(drawerBase, ctx)

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
  useBattleUpdater(props.stompClient).subscribeAfterWsConnect()
  useCommandsSender(props.stompClient).startSending()
})

function startBattle() {
  calculateBattleSize()
  calculateCanvasSize()
  calculateScaleCoefficient()
  props.stompClient.connect()
  useBattleProcessor().startProcessing()
}

function finishBattle() {
  clearCanvas()
  battleSize.value = undefined
  canvasSize.value = undefined
  scaleCoefficient.value = undefined
  props.stompClient.disconnect()
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
    shellDrawer.draw()
    groundDrawer.draw()
    vehicleDrawer.draw()
    explosionDrawer.draw()
  })
}

function clearCanvas() {
  if (ctx.value) {
    ctx.value.clearRect(0, 0, canvasSize.value.width, canvasSize.value.height)
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
