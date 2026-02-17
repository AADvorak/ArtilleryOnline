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
import BattleLinearProgress from "~/playground/components/BattleLinearProgress.vue";

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

const droneSpecs = computed(() => {
  return userVehicle.value?.config.drone
})

const progress = computed(() => {
  let value = 0
  const state = droneState.value
  const specs = droneSpecs.value
  if (state && specs) {
    if (state.prepareToLaunchRemainTime <= 0) {
      value = 1
    } else if (state.prepareToLaunchRemainTime < specs.prepareToLaunchTime) {
      value = (specs.prepareToLaunchTime - state.prepareToLaunchRemainTime) / specs.prepareToLaunchTime
    }
  }
  return value * 100
})

function launch() {
  commandsSender.sendCommand({
    command: Command.LAUNCH_DRONE
  })
}
</script>

<template>
  <div v-if="droneState" class="progress-wrapper ml-4">
    <battle-linear-progress
        :value="progress"
        :text="t('battleHeader.drones') + ': ' + droneState.remainDrones"
        color="#2196F3"
        clickable
        @click="launch"
    >
      <vertical-tooltip
          :location="VerticalTooltipLocation.BOTTOM"
          :tooltip="t('controls.launchDrone')"
          :show="globalStateStore.showHelp === VerticalTooltipLocation.BOTTOM"
      />
    </battle-linear-progress>
  </div>
</template>

<style scoped>
.progress-wrapper {
  min-width: 80px;
}
</style>
