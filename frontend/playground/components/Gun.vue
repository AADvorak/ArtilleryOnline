<script setup lang="ts">
import {computed} from 'vue'
import {useBattleStore} from '~/stores/battle'
import {useUserStore} from '~/stores/user'
import {useI18n} from "vue-i18n";
import {mdiCrosshairs, mdiCrosshairsOff} from "@mdi/js";
import {useCommandsSender} from "~/playground/composables/commands-sender";
import {Command} from "~/playground/data/command";
import {useGlobalStateStore} from "~/stores/global-state";
import {VerticalTooltipLocation} from "~/data/model";

const {t} = useI18n()

const commandsSender = useCommandsSender()

const userStore = useUserStore()
const battleStore = useBattleStore()
const globalStateStore = useGlobalStateStore()

const userVehicle = computed(() => {
  return battleStore.battle?.model.vehicles[userStore.user!.nickname]
})

const gunState = computed(() => {
  return userVehicle.value?.state.gunState
})

function switchMode() {
  commandsSender.sendCommand({
    command: Command.SWITCH_GUN_MODE
  })
}
</script>

<template>
  <icon-btn
      class="ml-2"
      v-if="!!gunState"
      :icon="gunState.fixed ? mdiCrosshairsOff : mdiCrosshairs"
      :tooltip="t('controls.switchGunMode')"
      :show-tooltip="globalStateStore.showHelp"
      :tooltip-location="VerticalTooltipLocation.BOTTOM"
      prevent-show-tooltip
      @click="switchMode"
  />
</template>
