<script setup lang="ts">
import {computed} from 'vue'
import {useBattleStore} from '~/stores/battle'
import {useUserStore} from '~/stores/user'
import {useI18n} from "vue-i18n";
import {useCommandsSender} from "~/playground/composables/commands-sender";
import {Command} from "~/playground/data/command";
import VerticalTooltip from "~/components/vertical-tooltip.vue";
import {useGlobalStateStore} from "~/stores/global-state";
import {VerticalTooltipLocation} from "~/data/model";

const {t} = useI18n()

const globalStateStore = useGlobalStateStore()

const commandsSender = useCommandsSender()

const userStore = useUserStore()
const battleStore = useBattleStore()

const userVehicle = computed(() => {
  return battleStore.battle?.model.vehicles[userStore.user!.nickname]
})

const droneState = computed(() => {
  return userVehicle.value?.state.droneState
})

function launch() {
  commandsSender.sendCommand({
    command: Command.LAUNCH_DRONE
  })
}
</script>

<template>
  <v-btn
      v-if="droneState && !droneState.launched"
      class="drone-btn"
      :color="droneState.readyToLaunch ? 'success' : 'warning'"
      @click="launch"
  >
    {{ t('battleHeader.drone') }}: {{
      droneState.readyToLaunch ? t('battleHeader.ready') : t('battleHeader.preparing')
    }}
    <vertical-tooltip
        :location="VerticalTooltipLocation.BOTTOM"
        :tooltip="t('controls.launchDrone')"
        :show="globalStateStore.showHelp === VerticalTooltipLocation.BOTTOM"
    />
  </v-btn>
</template>

<style scoped>
.drone-btn {
  padding: 0 8px;
}
</style>
