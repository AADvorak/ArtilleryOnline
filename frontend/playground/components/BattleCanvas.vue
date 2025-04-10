<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue'
import { useBattleStore } from '~/stores/battle'
import {useDrawerBase} from "@/playground/composables/drawer/drawer-base";
import {useVehicleDrawer} from "@/playground/composables/drawer/vehicle-drawer";
import {useShellDrawer} from "@/playground/composables/drawer/shell-drawer";
import {useExplosionDrawer} from "@/playground/composables/drawer/explosion-drawer";
import {useGroundDrawer} from "@/playground/composables/drawer/ground-drawer";
import {useMissileDrawer} from "~/playground/composables/drawer/missile-drawer";
import {useUserSettingsStore} from "~/stores/user-settings";
import {AppearancesNames} from "~/dictionary/appearances-names";
import {useDroneDrawer} from "~/playground/composables/drawer/drone-drawer";
import {useSurfaceDrawer} from "~/playground/composables/drawer/surface-drawer";
import {useParticleDrawer} from "~/playground/composables/drawer/particle-drawer";
import {useShellTrajectoryDrawer} from "~/playground/composables/drawer/shell-trajectory-drawer";

const HEADER_HEIGHT = 72

const battleStore = useBattleStore()
const userSettingsStore = useUserSettingsStore()

const appearances = computed(() => userSettingsStore.appearancesOrDefaultsNameValueMapping)
const battle = computed(() => battleStore.battle)
const battleSize = ref()
const canvasSize = ref()
const scaleCoefficient = ref()
const canvasClass = ref<string>('')
const canvasWidth = computed(() => {
  return canvasSize.value ? canvasSize.value.width : window.innerWidth
})
const canvasHeight = computed(() => {
  return canvasSize.value ? canvasSize.value.height : window.innerHeight
})
const canvasStyle = computed(() => {
  if (appearances.value[AppearancesNames.GROUND_TEXTURE_BACKGROUND] === '1') {
    return `background-image: url('/images/background-${battleStore.battle?.model.room.config.background}.jpg');`
  } else {
    return ''
  }
})

const canvas = ref<HTMLCanvasElement>()
const ctx = ref<CanvasRenderingContext2D>()

const drawerBase = useDrawerBase(scaleCoefficient, canvasSize)
const vehicleDrawer = useVehicleDrawer(drawerBase, ctx)
const shellDrawer = useShellDrawer(drawerBase, ctx)
const missileDrawer = useMissileDrawer(drawerBase, ctx)
const droneDrawer = useDroneDrawer(drawerBase, ctx)
const explosionDrawer = useExplosionDrawer(drawerBase, ctx)
const groundDrawer = useGroundDrawer(drawerBase, ctx)
const surfaceDrawer = useSurfaceDrawer(drawerBase, ctx)
const particleDrawer = useParticleDrawer(drawerBase, ctx)
const shellTrajectoryDrawer = useShellTrajectoryDrawer(drawerBase, ctx)

watch(battle, (value) => {
  if (value) {
    redrawBattle()
  }
})

onMounted(() => {
  initCanvasAndCtx()
  calculateBattleSize()
  calculateCanvasSize()
  calculateScaleCoefficient()
  calculateCanvasClass()
  addEventListener('resize', onWindowResize)
})

onBeforeUnmount(() => {
  removeEventListener('resize', onWindowResize)
})

function onWindowResize() {
  canvasSize.value = undefined
  scaleCoefficient.value = undefined
  calculateCanvasSize()
  calculateScaleCoefficient()
  if (canvas.value) {
    canvas.value.width = canvasWidth.value
    canvas.value.height = canvasHeight.value
  }
  calculateCanvasClass()
}

function calculateCanvasClass() {
  if (canvasHeight.value > window.innerHeight - HEADER_HEIGHT) {
    canvasClass.value = 'canvas-absolute-bottom'
  } else {
    canvasClass.value = ''
  }
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
    shellTrajectoryDrawer.draw()
    particleDrawer.draw()
    shellDrawer.draw()
    missileDrawer.draw()
    droneDrawer.draw()
    groundDrawer.draw()
    surfaceDrawer.draw()
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
    const screenWidth = window.innerWidth
    const screenHeight = window.innerHeight
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
        :style="canvasStyle"
        :class="canvasClass"
    ></canvas>
  </v-main>
</template>

<style scoped>
canvas {
  display: block;
  background-size: cover;
}

.canvas-absolute-bottom {
  bottom: 0;
  left: auto;
  transform: translateX(-50%);
  position: absolute;
}
</style>
