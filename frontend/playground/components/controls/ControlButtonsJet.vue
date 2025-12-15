<script setup lang="ts">
import {mdiTurbine} from "@mdi/js";
import IconBtn from "~/components/icon-btn.vue";
import {useI18n} from "vue-i18n";
import {useCommandsSender} from "~/playground/composables/commands-sender";
import {Command} from "~/playground/data/command";

const props = defineProps<{
  mouseEvents: boolean
  showTooltip: boolean
}>()

const {t} = useI18n()

const commandsSender = useCommandsSender()

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
  <icon-btn
      large prevent-show-tooltip
      :icon="mdiTurbine"
      :tooltip="t('controls.activateJet')"
      :show-tooltip="props.showTooltip"
      color="warning"
      @touchstart="jetOn"
      @touchend="jetOff"
      @mousedown="() => props.mouseEvents && jetOn()"
      @mouseup="() => props.mouseEvents && jetOff()"
  />
</template>
