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
  <div v-if="droneState && !droneState.launched" class="progress-wrapper">
    <v-progress-linear
        bg-color="blue-grey"
        height="16"
        :color="droneState.readyToLaunch ? 'green' : 'orange'"
        class="progress"
        :model-value="progress"
        @click="launch"
    >
      <span class="progress-text">{{ t('battleHeader.drone') }}</span>
      <vertical-tooltip
          :location="VerticalTooltipLocation.BOTTOM"
          :tooltip="t('controls.launchDrone')"
          :show="globalStateStore.showHelp === VerticalTooltipLocation.BOTTOM"
      />
    </v-progress-linear>
  </div>
</template>

<style scoped>
.progress-wrapper {
  min-width: 80px;
}
.progress {
  cursor: pointer;
}
.progress-text {
  font-size: 16px;
}
</style>
