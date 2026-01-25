<script setup lang="ts">
import {computed} from 'vue'
import {useBattleStore} from '~/stores/battle'
import {useUserStore} from '~/stores/user'
import {useCommandsSender} from "~/playground/composables/commands-sender";
import {Command} from "~/playground/data/command";
import {useGlobalStateStore} from "~/stores/global-state";
import {VerticalTooltipLocation} from "~/data/model";
import VerticalTooltip from "~/components/vertical-tooltip.vue";
import {useI18n} from "vue-i18n";

const {t} = useI18n()

const commandsSender = useCommandsSender()

const userStore = useUserStore()
const battleStore = useBattleStore()
const globalStateStore = useGlobalStateStore()

const userVehicle = computed(() => {
  return battleStore.battle?.model.vehicles[userStore.user!.nickname]
})

const missileLauncherState = computed(() => {
  return userVehicle.value?.state.missileLauncherState
})

const missileLauncherSpecs = computed(() => {
  return userVehicle.value?.config.missileLauncher
})

const progress = computed(() => {
  let value = 0
  const state = missileLauncherState.value
  const specs = missileLauncherSpecs.value
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
    command: Command.LAUNCH_MISSILE
  })
}
</script>

<template>
  <div v-if="missileLauncherState" class="progress-wrapper ml-2">
    <v-progress-linear
        bg-color="blue-grey"
        height="16"
        :color="missileLauncherState.prepareToLaunchRemainTime <= 0 ? '#2196F3' : '#778899'"
        class="progress"
        :model-value="progress"
        @click="launch"
    >
      <span class="progress-text">{{ t('battleHeader.missiles') }}: {{ missileLauncherState.remainMissiles }}</span>
      <vertical-tooltip
          :location="VerticalTooltipLocation.BOTTOM"
          :tooltip="t('controls.launchMissile')"
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
