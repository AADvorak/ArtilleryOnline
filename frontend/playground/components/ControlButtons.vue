<script setup lang="ts">
import {mdiArrowLeftBox, mdiArrowLeftCircle, mdiArrowRightBox, mdiArrowRightCircle, mdiTarget} from "@mdi/js";
import IconBtn from "~/components/icon-btn.vue";
import {useCommandsSender} from "~/playground/composables/commands-sender";
import {Command} from "~/playground/data/command";
import {MovingDirection} from "~/playground/data/common";
import {useI18n} from "vue-i18n";

const {t} = useI18n()

const commandsSender = useCommandsSender()

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
</script>

<template>
  <v-toolbar absolute class="bottom-left-toolbar" color="transparent">
    <icon-btn
        :icon="mdiArrowLeftBox"
        :tooltip="t('controls.moveLeft')"
        @mousedown="startMoveLeft"
        @mouseup="stopMoveLeft"
    />
    <icon-btn
        :icon="mdiArrowRightBox"
        :tooltip="t('controls.moveRight')"
        @mousedown="startMoveRight"
        @mouseup="stopMoveRight"
    />
    <v-spacer/>
  </v-toolbar>
  <v-toolbar absolute class="bottom-right-toolbar" color="transparent">
    <icon-btn
        :icon="mdiTarget"
        :tooltip="t('controls.shoot')"
        @mousedown="pushTrigger"
        @mouseup="releaseTrigger"
    />
    <icon-btn
        :icon="mdiArrowLeftCircle"
        :tooltip="t('controls.rotateGunLeft')"
        @mousedown="startRotateGunLeft"
        @mouseup="stopRotateGunLeft"
    />
    <icon-btn
        :icon="mdiArrowRightCircle"
        :tooltip="t('controls.rotateGunRight')"
        @mousedown="startRotateGunRight"
        @mouseup="stopRotateGunRight"
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
