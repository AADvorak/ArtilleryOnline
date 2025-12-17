<script setup lang="ts">
import {Joystick} from "@rbuljan/gamepad";
import {useCommandsSender} from "~/playground/composables/commands-sender";
import {Command} from "~/playground/data/command";
import {MovingDirection} from "~/playground/data/common";
import {useUserSettingsStore} from "~/stores/user-settings";
import {AppearancesNames} from "~/dictionary/appearances-names";
import {ControlButtonsAlignments} from "~/dictionary/control-buttons-alignments";
import {useGlobalStateStore} from "~/stores/global-state";
import {VerticalTooltipLocation} from "~/data/model";
import VerticalTooltip from "~/components/vertical-tooltip.vue";
import {useI18n} from "vue-i18n";
import {
  mdiTarget,
  mdiTurbine,
  mdiArrowLeftCircle,
  mdiArrowRightCircle,
  mdiArrowLeftBox,
  mdiArrowRightBox
} from "@mdi/js";

const THRESHOLD = 0.3

const {t} = useI18n()

const userSettingsStore = useUserSettingsStore()
const globalStateStore = useGlobalStateStore()
const commandsSender = useCommandsSender()

const leftJoystick = ref<Joystick | undefined>(undefined)
const rightJoystick = ref<Joystick | undefined>(undefined)

const moveControl = ref(0)
const rotateControl = ref(0)
const jetControl = ref(0)
const shootControl = ref(0)

const showTooltip = computed(() => globalStateStore.showHelp === VerticalTooltipLocation.TOP)

const controlsAlignment = computed(() =>
    userSettingsStore.appearancesOrDefaultsNameValueMapping[AppearancesNames.CONTROL_BUTTONS_ALIGNMENT])

const leftJoystickClass = computed(() => {
  switch (controlsAlignment.value) {
    case ControlButtonsAlignments.BOTTOM_HORIZONTAL:
      return 'joystick bottom-left-joystick'
    case ControlButtonsAlignments.CENTER_VERTICAL:
      return 'joystick center-left-joystick'
    default:
      return ''
  }
})

const rightJoystickClass = computed(() => {
  switch (controlsAlignment.value) {
    case ControlButtonsAlignments.BOTTOM_HORIZONTAL:
      return 'joystick bottom-right-joystick'
    case ControlButtonsAlignments.CENTER_VERTICAL:
      return 'joystick center-right-joystick'
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
      moveControl.value = state.value * Math.cos(state.angle)
      jetControl.value = state.value * Math.sin(state.angle)
    },
  })
  leftJoystick.value.init()

  rightJoystick.value = new Joystick({
    id: 'right-joystick',
    parentElement: document.querySelector('#right-joystick-div'),
    spring: true,
    onInput(state) {
      rotateControl.value = state.value * Math.cos(state.angle)
      shootControl.value = state.value * Math.sin(state.angle)
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
  <div id="left-joystick-div" :class="leftJoystickClass">
    <v-icon
        class="icon-micro icon-top"
        color="warning"
        :icon="mdiTurbine"
    />
    <v-icon
        class="icon-micro icon-left"
        :icon="mdiArrowLeftBox"
    />
    <v-icon
        class="icon-micro icon-right"
        :icon="mdiArrowRightBox"
    />
    <vertical-tooltip
        :tooltip="'< ' + t('controls.move') + ' >'"
        :show="showTooltip"
    />
    <vertical-tooltip
        :tooltip="'< ' + t('controls.activateJet') + ' >'"
        :show="showTooltip"
        :location="VerticalTooltipLocation.TOP"
    />
  </div>
  <div id="right-joystick-div" :class="rightJoystickClass">
    <v-icon
        class="icon-micro icon-top"
        color="error"
        :icon="mdiTarget"
    />
    <v-icon
        class="icon-micro icon-left"
        :icon="mdiArrowLeftCircle"
    />
    <v-icon
        class="icon-micro icon-right"
        :icon="mdiArrowRightCircle"
    />
    <vertical-tooltip
        :tooltip="'< ' + t('controls.rotateGun') + ' >'"
        :show="showTooltip"
    />
    <vertical-tooltip
        :tooltip="'< ' + t('controls.shoot') + ' >'"
        :show="showTooltip"
        :location="VerticalTooltipLocation.TOP"
    />
  </div>
</template>

<style scoped>
.joystick {
  width: 84px;
  height: 84px;
}

.bottom-left-joystick {
  position: fixed;
  bottom: 8px;
  left: 8px;
}

.bottom-right-joystick {
  position: fixed;
  bottom: 8px;
  right: 8px;
}

.center-left-joystick {
  position: fixed;
  bottom: 50%;
  left: 8px;
}

.center-right-joystick {
  position: fixed;
  bottom: 50%;
  right: 8px;
}

.icon-micro {
  position: absolute;
  width: 14px;
  height: 14px;
}

.icon-top {
  top: 4px;
  left: 31px;
}

.icon-right {
  top: 35px;
  right: 0;
}

.icon-left {
  top: 35px;
  left: 0;
}
</style>
