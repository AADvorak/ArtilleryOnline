<script setup lang="ts">
import {mdiArrowLeftBox, mdiArrowRightBox} from "@mdi/js";
import IconBtn from "~/components/icon-btn.vue";
import {useI18n} from "vue-i18n";
import {useCommandsSender} from "~/playground/composables/commands-sender";
import {Command} from "~/playground/data/command";
import {MovingDirection} from "~/playground/data/common";

const props = defineProps<{
  mouseEvents: boolean
  showTooltip: boolean
}>()

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
</script>

<template>
  <icon-btn
      large prevent-show-tooltip
      :icon="mdiArrowLeftBox"
      :tooltip="t('controls.moveLeft')"
      :show-tooltip="props.showTooltip"
      @touchstart="startMoveLeft"
      @touchend="stopMoveLeft"
      @mousedown="() => props.mouseEvents && startMoveLeft()"
      @mouseup="() => props.mouseEvents && stopMoveLeft()"
  />
  <icon-btn
      large prevent-show-tooltip
      :icon="mdiArrowRightBox"
      :tooltip="t('controls.moveRight')"
      :show-tooltip="props.showTooltip"
      @touchstart="startMoveRight"
      @touchend="stopMoveRight"
      @mousedown="() => props.mouseEvents && startMoveRight()"
      @mouseup="() => props.mouseEvents && stopMoveRight()"
  />
</template>
