<script setup lang="ts">
import { Joystick } from "@rbuljan/gamepad";
import {useCommandsSender} from "~/playground/composables/commands-sender";
import {Command} from "~/playground/data/command";
import {MovingDirection} from "~/playground/data/common";
import {useUserSettingsStore} from "~/stores/user-settings";
import {AppearancesNames} from "~/dictionary/appearances-names";
import {ControlButtonsAlignments} from "~/dictionary/control-buttons-alignments";

const THRESHOLD = 25

const userSettingsStore = useUserSettingsStore()
const commandsSender = useCommandsSender()

const leftJoystick = ref<Joystick | undefined>(undefined)
const rightJoystick = ref<Joystick | undefined>(undefined)

const moveControl = ref(0)
const rotateControl = ref(0)
const jetControl = ref(0)
const shootControl = ref(0)

const controlsAlignment = computed(() =>
    userSettingsStore.appearancesOrDefaultsNameValueMapping[AppearancesNames.CONTROL_BUTTONS_ALIGNMENT])

const leftJoystickClass = computed(() => {
  switch (controlsAlignment.value) {
    case ControlButtonsAlignments.BOTTOM_HORIZONTAL:
      return 'bottom-left-joystick'
    case ControlButtonsAlignments.CENTER_VERTICAL:
      return 'center-left-joystick'
    default:
      return ''
  }
})

const rightJoystickClass = computed(() => {
  switch (controlsAlignment.value) {
    case ControlButtonsAlignments.BOTTOM_HORIZONTAL:
      return 'bottom-right-joystick'
    case ControlButtonsAlignments.CENTER_VERTICAL:
      return 'center-right-joystick'
    default:
      return ''
  }
})

watch(moveControl, (value, oldValue) => {
  if (value > THRESHOLD && oldValue <= THRESHOLD) {
    commandsSender.sendCommand({
      command: Command.START_MOVING,
      params: {direction: MovingDirection.RIGHT}
    })
  }
  if (value < -THRESHOLD && oldValue >= -THRESHOLD) {
    commandsSender.sendCommand({
      command: Command.START_MOVING,
      params: {direction: MovingDirection.LEFT}
    })
  }
  if (Math.abs(value) <= THRESHOLD && Math.abs(oldValue) > THRESHOLD) {
    commandsSender.sendCommand({
      command: Command.STOP_MOVING,
      params: {}
    })
  }
})

watch(rotateControl, (value, oldValue) => {
  if (value > THRESHOLD && oldValue <= THRESHOLD) {
    commandsSender.sendCommand({
      command: Command.START_GUN_ROTATING,
      params: {direction: MovingDirection.RIGHT}
    })
  }
  if (value < -THRESHOLD && oldValue >= -THRESHOLD) {
    commandsSender.sendCommand({
      command: Command.START_GUN_ROTATING,
      params: {direction: MovingDirection.LEFT}
    })
  }
  if (Math.abs(value) <= THRESHOLD && Math.abs(oldValue) > THRESHOLD) {
    commandsSender.sendCommand({
      command: Command.STOP_GUN_ROTATING,
      params: {}
    })
  }
})

watch(jetControl, (value, oldValue) => {
  if (Math.abs(value) > THRESHOLD && Math.abs(oldValue) <= THRESHOLD) {
    commandsSender.sendCommand({
      command: Command.JET_ON
    })
  }
  if (Math.abs(value) <= THRESHOLD && Math.abs(oldValue) > THRESHOLD) {
    commandsSender.sendCommand({
      command: Command.JET_OFF
    })
  }
})

watch(shootControl, (value, oldValue) => {
  if (Math.abs(value) > THRESHOLD && Math.abs(oldValue) <= THRESHOLD) {
    commandsSender.sendCommand({
      command: Command.PUSH_TRIGGER
    })
  }
  if (Math.abs(value) <= THRESHOLD && Math.abs(oldValue) > THRESHOLD) {
    commandsSender.sendCommand({
      command: Command.RELEASE_TRIGGER
    })
  }
})

onMounted(() => {
  leftJoystick.value = new Joystick({
    id: 'left-joystick',
    parentElement: document.querySelector('#left-joystick-div'),
    spring: true,
    onInput(state) {
      moveControl.value = state.x_diff
      jetControl.value = state.y_diff
    },
  })
  leftJoystick.value.init()

  rightJoystick.value = new Joystick({
    id: 'right-joystick',
    parentElement: document.querySelector('#right-joystick-div'),
    spring: true,
    onInput(state) {
      rotateControl.value = state.x_diff
      shootControl.value = state.y_diff
    },
  })
  rightJoystick.value.init()
})

onUnmounted(() => {
  leftJoystick.value && leftJoystick.value.destroy()
  rightJoystick.value && rightJoystick.value.destroy()
})
</script>

<template>
  <div id="left-joystick-div" :class="leftJoystickClass"></div>
  <div id="right-joystick-div" :class="rightJoystickClass"></div>
</template>

<style scoped>
.bottom-left-joystick {
  position: fixed;
  bottom: 50px;
  left: 50px;
}

.bottom-right-joystick {
  position: fixed;
  bottom: 50px;
  right: 50px;
}

.center-left-joystick {
  position: fixed;
  bottom: 50%;
  left: 50px;
}

.center-right-joystick {
  position: fixed;
  bottom: 50%;
  right: 50px;
}
</style>
