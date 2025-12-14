<script setup lang="ts">
import {
  mdiArrowLeftBox,
  mdiArrowLeftCircle,
  mdiArrowRightBox,
  mdiArrowRightCircle,
  mdiTarget,
  mdiTurbine
} from "@mdi/js";
import IconBtn from "~/components/icon-btn.vue";
import {useCommandsSender} from "~/playground/composables/commands-sender";
import {Command} from "~/playground/data/command";
import {MovingDirection} from "~/playground/data/common";
import {useI18n} from "vue-i18n";
import {useGlobalStateStore} from "~/stores/global-state";
import {VerticalTooltipLocation} from "~/data/model";
import {useUserSettingsStore} from "~/stores/user-settings";
import {ControlButtonsAlignments} from "~/dictionary/control-buttons-alignments";
import {AppearancesNames} from "~/dictionary/appearances-names";

const props = defineProps<{
  mouseEvents: boolean
}>()

const {t} = useI18n()

const commandsSender = useCommandsSender()
const globalStateStore = useGlobalStateStore()
const userSettingsStore = useUserSettingsStore()

const showTooltip = computed(() => globalStateStore.showHelp === VerticalTooltipLocation.TOP)

const controlButtonsAlignment = computed(() =>
    userSettingsStore.appearancesOrDefaultsNameValueMapping[AppearancesNames.CONTROL_BUTTONS_ALIGNMENT])

const leftToolbarClass = computed(() => {
  switch (controlButtonsAlignment.value) {
    case ControlButtonsAlignments.BOTTOM_HORIZONTAL:
      return 'bottom-left-controls'
    case ControlButtonsAlignments.CENTER_VERTICAL:
      return 'left-controls'
    default:
      return ''
  }
})

const rightToolbarClass = computed(() => {
  switch (controlButtonsAlignment.value) {
    case ControlButtonsAlignments.BOTTOM_HORIZONTAL:
      return 'bottom-right-controls'
    case ControlButtonsAlignments.CENTER_VERTICAL:
      return 'right-controls'
    default:
      return ''
  }
})

const innerToolbarClass = computed(() => {
  switch (controlButtonsAlignment.value) {
    case ControlButtonsAlignments.CENTER_VERTICAL:
      return 'd-flex flex-column align-center'
    default:
      return ''
  }
})

const leftButtonClass = computed(() => {
  switch (controlButtonsAlignment.value) {
    case ControlButtonsAlignments.BOTTOM_HORIZONTAL:
      return 'mr-2'
    case ControlButtonsAlignments.CENTER_VERTICAL:
      return 'mb-2'
    default:
      return ''
  }
})

const rightButtonClass = computed(() => {
  switch (controlButtonsAlignment.value) {
    case ControlButtonsAlignments.BOTTOM_HORIZONTAL:
      return 'ml-2'
    case ControlButtonsAlignments.CENTER_VERTICAL:
      return 'mb-2'
    default:
      return ''
  }
})

function startMoveRight() {
  commandsSender.sendCommand({
    command: Command.START_MOVING,
    params: {direction: MovingDirection.RIGHT}
  })
}

function stopMoveRight() {
  commandsSender.sendCommand({
    command: Command.STOP_MOVING,
    params: {direction: MovingDirection.RIGHT}
  })
}

function startMoveLeft() {
  commandsSender.sendCommand({
    command: Command.START_MOVING,
    params: {direction: MovingDirection.LEFT}
  })
}

function stopMoveLeft() {
  commandsSender.sendCommand({
    command: Command.STOP_MOVING,
    params: {direction: MovingDirection.LEFT}
  })
}

function startRotateGunRight() {
  commandsSender.sendCommand({
    command: Command.START_GUN_ROTATING,
    params: {direction: MovingDirection.RIGHT}
  })
}

function stopRotateGunRight() {
  commandsSender.sendCommand({
    command: Command.STOP_GUN_ROTATING,
    params: {direction: MovingDirection.RIGHT}
  })
}

function startRotateGunLeft() {
  commandsSender.sendCommand({
    command: Command.START_GUN_ROTATING,
    params: {direction: MovingDirection.LEFT}
  })
}

function stopRotateGunLeft() {
  commandsSender.sendCommand({
    command: Command.STOP_GUN_ROTATING,
    params: {direction: MovingDirection.LEFT}
  })
}

function pushTrigger() {
  commandsSender.sendCommand({
    command: Command.PUSH_TRIGGER
  })
}

function releaseTrigger() {
  commandsSender.sendCommand({
    command: Command.RELEASE_TRIGGER
  })
}

function jetOn() {
  commandsSender.sendCommand({
    command: Command.JET_ON
  })
}

function jetOff() {
  commandsSender.sendCommand({
    command: Command.JET_OFF
  })
}
</script>

<template>
  <v-toolbar absolute :class="leftToolbarClass" color="transparent">
    <div :class="innerToolbarClass">
      <icon-btn
          large prevent-show-tooltip
          :class="leftButtonClass"
          :icon="mdiArrowLeftBox"
          :tooltip="t('controls.moveLeft')"
          :show-tooltip="showTooltip"
          @touchstart="startMoveLeft"
          @touchend="stopMoveLeft"
          @mousedown="() => props.mouseEvents && startMoveLeft()"
          @mouseup="() => props.mouseEvents && stopMoveLeft()"
      />
      <icon-btn
          large prevent-show-tooltip
          :class="leftButtonClass"
          :icon="mdiArrowRightBox"
          :tooltip="t('controls.moveRight')"
          :show-tooltip="showTooltip"
          @touchstart="startMoveRight"
          @touchend="stopMoveRight"
          @mousedown="() => props.mouseEvents && startMoveRight()"
          @mouseup="() => props.mouseEvents && stopMoveRight()"
      />
      <icon-btn
          large prevent-show-tooltip
          :icon="mdiTarget"
          :tooltip="t('controls.shoot')"
          :show-tooltip="showTooltip"
          color="error"
          @touchstart="pushTrigger"
          @touchend="releaseTrigger"
          @mousedown="() => props.mouseEvents && pushTrigger()"
          @mouseup="() => props.mouseEvents && releaseTrigger()"
      />
    </div>
  </v-toolbar>

  <v-toolbar absolute :class="rightToolbarClass" color="transparent">
    <div :class="innerToolbarClass">
      <icon-btn
          large prevent-show-tooltip
          :class="rightButtonClass"
          :icon="mdiTarget"
          :tooltip="t('controls.shoot')"
          :show-tooltip="showTooltip"
          color="error"
          @touchstart="pushTrigger"
          @touchend="releaseTrigger"
          @mousedown="() => props.mouseEvents && pushTrigger()"
          @mouseup="() => props.mouseEvents && releaseTrigger()"
      />
      <icon-btn
          large prevent-show-tooltip
          :class="rightButtonClass"
          :icon="mdiTurbine"
          :tooltip="t('controls.activateJet')"
          :show-tooltip="showTooltip"
          color="warning"
          @touchstart="jetOn"
          @touchend="jetOff"
          @mousedown="() => props.mouseEvents && jetOn()"
          @mouseup="() => props.mouseEvents && jetOff()"
      />
      <icon-btn
          large prevent-show-tooltip
          :class="rightButtonClass"
          :icon="mdiArrowLeftCircle"
          :tooltip="t('controls.rotateGunLeft')"
          :show-tooltip="showTooltip"
          @touchstart="startRotateGunLeft"
          @touchend="stopRotateGunLeft"
          @mousedown="() => props.mouseEvents && startRotateGunLeft()"
          @mouseup="() => props.mouseEvents && stopRotateGunLeft()"
      />
      <icon-btn
          large prevent-show-tooltip
          :icon="mdiArrowRightCircle"
          :tooltip="t('controls.rotateGunRight')"
          :show-tooltip="showTooltip"
          @touchstart="startRotateGunRight"
          @touchend="stopRotateGunRight"
          @mousedown="() => props.mouseEvents && startRotateGunRight()"
          @mouseup="() => props.mouseEvents && stopRotateGunRight()"
      />
    </div>
  </v-toolbar>
</template>

<style scoped>
.bottom-left-controls {
  bottom: 0;
  left: 0;
  width: auto;
}

.bottom-right-controls {
  bottom: 0;
  right: 0;
  width: auto;
}

.left-controls {
  left: 16px;
  top: 50%;
  transform: translateY(-50%);
  width: auto;
  height: auto;
  background: transparent !important;
}

.right-controls {
  right: 16px;
  top: 50%;
  transform: translateY(-50%);
  width: auto;
  height: auto;
  background: transparent !important;
}

.left-controls :deep(.v-toolbar__content),
.right-controls :deep(.v-toolbar__content) {
  padding: 0;
  height: auto !important;
  display: flex;
  flex-direction: column;
}

.d-flex.flex-column.align-center {
  gap: 8px;
}
</style>
