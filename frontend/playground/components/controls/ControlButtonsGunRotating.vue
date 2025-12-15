<script setup lang="ts">
import {mdiArrowLeftCircle, mdiArrowRightCircle} from "@mdi/js";
import IconBtn from "~/components/icon-btn.vue";
import {useI18n} from "vue-i18n";
import {useCommandsSender} from "~/playground/composables/commands-sender";
import {Command} from "~/playground/data/command";
import {MovingDirection} from "~/playground/data/common";
import type {VerticalTooltipLocation} from "~/data/model";

const props = defineProps<{
  mouseEvents: boolean
  showTooltip: boolean
  tooltipLocation?: VerticalTooltipLocation
}>()

const {t} = useI18n()

const commandsSender = useCommandsSender()

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
</script>

<template>
  <icon-btn
      large prevent-show-tooltip
      :icon="mdiArrowLeftCircle"
      :tooltip="t('controls.rotateGun')"
      :show-tooltip="props.showTooltip"
      :tooltip-location="props.tooltipLocation"
      @touchstart="startRotateGunLeft"
      @touchend="stopRotateGunLeft"
      @mousedown="() => props.mouseEvents && startRotateGunLeft()"
      @mouseup="() => props.mouseEvents && stopRotateGunLeft()"
  />
  <icon-btn
      large prevent-show-tooltip
      :icon="mdiArrowRightCircle"
      :tooltip="t('controls.rotateGun')"
      @touchstart="startRotateGunRight"
      @touchend="stopRotateGunRight"
      @mousedown="() => props.mouseEvents && startRotateGunRight()"
      @mouseup="() => props.mouseEvents && stopRotateGunRight()"
  />
</template>
