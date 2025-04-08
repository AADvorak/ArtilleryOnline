<script setup lang="ts">
import {mdiArrowLeftBox, mdiArrowLeftCircle, mdiArrowRightBox,
  mdiArrowRightCircle, mdiTarget, mdiTurbine} from "@mdi/js";
import IconBtn from "~/components/icon-btn.vue";
import {useCommandsSender} from "~/playground/composables/commands-sender";
import {Command} from "~/playground/data/command";
import {MovingDirection} from "~/playground/data/common";
import {useI18n} from "vue-i18n";
import {useGlobalStateStore} from "~/stores/global-state";

const {t} = useI18n()

const commandsSender = useCommandsSender()
const globalStateStore = useGlobalStateStore()

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
  <v-toolbar absolute class="bottom-left-toolbar" color="transparent">
    <icon-btn
        class="mr-2" large prevent-show-tooltip
        :icon="mdiArrowLeftBox"
        :tooltip="t('controls.moveLeft')"
        :show-tooltip="globalStateStore.showHelp"
        @touchstart="startMoveLeft"
        @touchend="stopMoveLeft"
    />
    <icon-btn
        class="mr-2" large prevent-show-tooltip
        :icon="mdiArrowRightBox"
        :tooltip="t('controls.moveRight')"
        :show-tooltip="globalStateStore.showHelp"
        @touchstart="startMoveRight"
        @touchend="stopMoveRight"
    />
    <icon-btn
        class="mr-2" large prevent-show-tooltip
        :icon="mdiTarget"
        :tooltip="t('controls.shoot')"
        :show-tooltip="globalStateStore.showHelp"
        color="error"
        @touchstart="pushTrigger"
        @touchend="releaseTrigger"
    />
  </v-toolbar>
  <v-toolbar absolute class="bottom-right-toolbar" color="transparent">
    <icon-btn
        class="ml-2" large prevent-show-tooltip
        :icon="mdiTarget"
        :tooltip="t('controls.shoot')"
        :show-tooltip="globalStateStore.showHelp"
        color="error"
        @touchstart="pushTrigger"
        @touchend="releaseTrigger"
    />
    <icon-btn
        class="ml-2" large prevent-show-tooltip
        :icon="mdiTurbine"
        :tooltip="t('controls.activateJet')"
        :show-tooltip="globalStateStore.showHelp"
        color="warning"
        @touchstart="jetOn"
        @touchend="jetOff"
    />
    <icon-btn
        class="ml-2" large prevent-show-tooltip
        :icon="mdiArrowLeftCircle"
        :tooltip="t('controls.rotateGunLeft')"
        :show-tooltip="globalStateStore.showHelp"
        @touchstart="startRotateGunLeft"
        @touchend="stopRotateGunLeft"
    />
    <icon-btn
        class="ml-2" large prevent-show-tooltip
        :icon="mdiArrowRightCircle"
        :tooltip="t('controls.rotateGunRight')"
        :show-tooltip="globalStateStore.showHelp"
        @touchstart="startRotateGunRight"
        @touchend="stopRotateGunRight"
    />
  </v-toolbar>
</template>

<style scoped>
.bottom-left-toolbar {
  bottom: 0;
  left: 0;
  width: auto;
}

.bottom-right-toolbar {
  bottom: 0;
  right: 0;
  width: auto;
}
</style>
