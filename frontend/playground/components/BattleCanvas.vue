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
import {useBoxDrawer} from "~/playground/composables/drawer/box-drawer";
import {BattlefieldAlignments} from "~/dictionary/battlefield-alignments";
import {useUserStore} from "~/stores/user";

const HEADER_HEIGHT = 72
const SCROLL_RESERVE_WIDTH = 40

const battleStore = useBattleStore()
const userStore = useUserStore()
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
const alignByScreenHeight = computed(() =>
    appearances.value[AppearancesNames.BATTLEFIELD_ALIGNMENT] === BattlefieldAlignments.BY_SCREEN_HEIGHT)
const userVehicleRelativePosition = computed(() => {
  const userVehicle = battleStore.battle?.model.vehicles[userStore.user!.nickname]
  if (userVehicle) {
    const xMin = battleStore.battle?.model.room.specs.leftBottom.x
    const xMax = battleStore.battle?.model.room.specs.rightTop.x
    const xVehicle = userVehicle.state.position.x
    return (xVehicle - xMin) / (xMax - xMin)
  }
})

const canvas = ref<HTMLCanvasElement>()
const scroll = ref<HTMLElement>()
const ctx = ref<CanvasRenderingContext2D>()

const drawerBase = useDrawerBase(scaleCoefficient, canvasSize)
const vehicleDrawer = useVehicleDrawer(drawerBase, ctx)
const shellDrawer = useShellDrawer(drawerBase, ctx)
const missileDrawer = useMissileDrawer(drawerBase, ctx)
const droneDrawer = useDroneDrawer(drawerBase, ctx)
const boxDrawer = useBoxDrawer(drawerBase, ctx)
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
  if (alignByScreenHeight.value) {
    setTimeout(scrollToVehicle)
  }
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
  const classes = []
  if (canvasHeight.value > window.innerHeight - HEADER_HEIGHT) {
    classes.push('canvas-absolute-bottom')
  }
  if (alignByScreenHeight.value) {
    classes.push('canvas-scroll')
  }
  canvasClass.value = classes.join(' ')
}

function initCanvasAndCtx() {
  canvas.value = document.getElementById('battle-canvas') as HTMLCanvasElement
  scroll.value = document.getElementById('canvas-scroll') as HTMLElement
  if (canvas.value && canvas.value.getContext) {
    ctx.value = canvas.value.getContext('2d') as CanvasRenderingContext2D
  }
}

function scrollToVehicle() {
  if (!userVehicleRelativePosition.value) {
    return
  }
  const cnvWidth = canvasWidth.value
  const wndWidth = window.innerWidth
  const maxLeft = cnvWidth - wndWidth
  if (maxLeft > 0 && scroll.value) {
    const vehiclePosition = userVehicleRelativePosition.value * cnvWidth
    const left = scroll.value.scrollLeft
    if (vehiclePosition < left + SCROLL_RESERVE_WIDTH || vehiclePosition > left + wndWidth - SCROLL_RESERVE_WIDTH) {
      let newLeft = vehiclePosition - wndWidth / 2
      if (newLeft > maxLeft) newLeft = maxLeft
      if (newLeft < 0) newLeft = 0
      scroll.value.scrollTo({
        top: 0,
        left: newLeft,
        behavior: 'smooth'
      })
    }
  }
  setTimeout(scrollToVehicle, 2000)
}

function redrawBattle() {
  requestAnimationFrame(() => {
    clearCanvas()
    shellTrajectoryDrawer.draw()
    particleDrawer.draw()
    shellDrawer.draw()
    missileDrawer.draw()
    droneDrawer.draw()
    boxDrawer.draw()
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
    if (alignByScreenHeight.value || battleWidthToHeight < screenWidthToHeight) {
      const height = screenHeight
      const width = Math.floor(battleWidthToHeight * height)
      canvasSize.value = { width, height }
    } else {
      const width = screenWidth
      const height = Math.floor(width / battleWidthToHeight)
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

function preventArrowKeysEvents(event) {
  if (["ArrowUp", "ArrowDown", "ArrowLeft", "ArrowRight"].includes(event.key)) {
    event.preventDefault()
    event.stopPropagation()
  }
}
</script>

<template>
  <v-main>
    <div
        id="canvas-scroll"
        :class="canvasClass"
        @keydown="preventArrowKeysEvents"
    >
      <canvas
          id="battle-canvas"
          :width="canvasWidth"
          :height="canvasHeight"
          :style="canvasStyle"
      ></canvas>
    </div>
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

.canvas-scroll {
  overflow-x: auto;       /* Enables horizontal scrolling */
  overflow-y: hidden;     /* Prevent vertical scrolling */
  width: 100%;            /* Or a fixed width, e.g., 600px */
  max-width: 100%;        /* Prevent container overflow */
  /* optionally add a border or padding as needed */
}
</style>
