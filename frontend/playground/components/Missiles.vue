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

const missileLauncher = computed(() => {
  return userVehicle.value?.state.missileLauncherState
})

function launch() {
  commandsSender.sendCommand({
    command: Command.LAUNCH_MISSILE
  })
}
</script>

<template>
  <no-focus-btn
      v-if="missileLauncher"
      class="missile-btn"
      color="primary"
      :disabled="!missileLauncher.remainMissiles"
      @click="launch"
  >
    MSL: {{ missileLauncher.remainMissiles }}
    <vertical-tooltip
        :location="VerticalTooltipLocation.BOTTOM"
        :tooltip="t('controls.launchMissile')"
        :show="globalStateStore.showHelp === VerticalTooltipLocation.BOTTOM"
    />
  </no-focus-btn>
</template>

<style scoped>
.missile-btn {
  padding: 0 8px;
}
</style>
