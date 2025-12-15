<script setup lang="ts">
import {mdiTarget} from "@mdi/js";
import IconBtn from "~/components/icon-btn.vue";
import {useI18n} from "vue-i18n";
import {useCommandsSender} from "~/playground/composables/commands-sender";
import {Command} from "~/playground/data/command";

const props = defineProps<{
  mouseEvents: boolean
  buttonClass: string
  showTooltip: boolean
}>()

const {t} = useI18n()

const commandsSender = useCommandsSender()

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
  <icon-btn
      large prevent-show-tooltip
      :class="props.buttonClass"
      :icon="mdiTarget"
      :tooltip="t('controls.shoot')"
      :show-tooltip="props.showTooltip"
      color="error"
      @touchstart="pushTrigger"
      @touchend="releaseTrigger"
      @mousedown="() => props.mouseEvents && pushTrigger()"
      @mouseup="() => props.mouseEvents && releaseTrigger()"
  />
</template>
