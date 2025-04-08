<script setup lang="ts">
import {computed} from "vue";
import {useBattleStore} from "~/stores/battle";
import ReloadingProgress from "@/playground/components/ReloadingProgress.vue";
import HitPointsBar from "@/playground/components/HitPointsBar.vue";
import BattleTimer from "@/playground/components/BattleTimer.vue";
import BattleDebugButtons from "@/playground/components/BattleDebugButtons.vue";
import {useSettingsStore} from "~/stores/settings";
import JetBar from "~/playground/components/JetBar.vue";
import LeaveBattleDialog from "~/playground/components/LeaveBattleDialog.vue";
import {useUserStore} from "~/stores/user";
import { mdiCloseThick, mdiHelp } from '@mdi/js'
import IconBtn from "~/components/icon-btn.vue";
import HelpDialog from "~/playground/components/HelpDialog.vue";
import {useUserSettingsStore} from "~/stores/user-settings";
import {AppearancesNames} from "~/dictionary/appearances-names";
import {useI18n} from "vue-i18n";
import BattleFps from "~/playground/components/BattleFps.vue";
import BattlePing from "~/playground/components/BattlePing.vue";
import Missiles from "~/playground/components/Missiles.vue";
import Drone from "~/playground/components/Drone.vue";
import Bomber from "~/playground/components/Bomber.vue";
import Gun from "~/playground/components/Gun.vue";
import {useGlobalStateStore} from "~/stores/global-state";

const {t} = useI18n()
const battleStore = useBattleStore()
const userStore = useUserStore()
const settingsStore = useSettingsStore()
const userSettingsStore = useUserSettingsStore()
const globalStateStore = useGlobalStateStore()

const leaveBattleDialog = ref<InstanceType<typeof LeaveBattleDialog> | null>(null)
const helpDialog = ref<InstanceType<typeof HelpDialog> | null>(null)

const appearances = computed(() => userSettingsStore.appearancesOrDefaultsNameValueMapping)
const showControlButtons = computed(() => appearances.value[AppearancesNames.SHOW_CONTROL_BUTTONS] === '1')

const userKeys = computed(() => {
  if (appearances.value[AppearancesNames.ALL_HP_TOP] === '1') {
    return Object.keys(battleStore.vehicles || [])
  }
  return [userStore.user!.nickname]
})

const userKeyPairs = computed(() => {
  const usersCount = userKeys.value.length
  const pairsCount = Math.ceil(usersCount / 2)
  const pairs = []
  for (let pairNumber = 0; pairNumber < pairsCount; pairNumber++) {
    const pair = []
    const first = userKeys.value[2 * pairNumber]
    first && pair.push(first)
    const second = userKeys.value[2 * pairNumber + 1]
    second && pair.push(second)
    pairs.push(pair)
  }
  return pairs
})

const jetAvailable = computed(() => {
  const vehicle = battleStore.battle?.model.vehicles[userStore.user!.nickname]
  return !!vehicle && !!vehicle.config.jet
})

const isDebugMode = computed(() => settingsStore.settings?.debug)

function showLeaveBattleDialog() {
  leaveBattleDialog.value?.show()
}

function showHelp() {
  if (showControlButtons.value && !globalStateStore.showHelp) {
    globalStateStore.showHelp = true
    setTimeout(() => globalStateStore.showHelp = false, 5000)
  } else {
    helpDialog.value?.show()
  }
}
</script>

<template>
  <v-app-bar color="transparent" flat density="compact">
    <div v-if="isDebugMode" class="ml-5">
      <BattleDebugButtons />
    </div>
    <div class="ml-5 battle-timer-wrapper">
      <BattleTimer />
    </div>
    <div class="ml-5 battle-fps-wrapper">
      <BattleFps />
    </div>
    <div class="ml-5 battle-ping-wrapper">
      <BattlePing />
    </div>
    <div class="ml-5 hit-points-bar-wrapper" v-for="userKeyPair in userKeyPairs">
      <HitPointsBar v-for="userKey in userKeyPair" :user-key="userKey" />
    </div>
    <v-spacer/>
    <icon-btn
        :icon="mdiHelp"
        :tooltip="t('common.help')"
        @click="showHelp"
    />
    <messages-menu/>
    <icon-btn
        :icon="mdiCloseThick"
        :tooltip="t('battleHeader.leaveBattle')"
        @click="showLeaveBattleDialog"
    />
    <LeaveBattleDialog ref="leaveBattleDialog"/>
    <HelpDialog ref="helpDialog"/>
  </v-app-bar>
  <v-app-bar color="transparent" flat density="compact">
    <div v-if="jetAvailable" class="ml-5 jet-bar-wrapper">
      <JetBar />
    </div>
    <Gun />
    <Missiles />
    <Drone />
    <Bomber />
    <ReloadingProgress />
  </v-app-bar>
</template>

<style scoped>
.battle-timer-wrapper {
  min-width: 50px;
}

.battle-fps-wrapper {
  min-width: 50px;
}

.battle-ping-wrapper {
  min-width: 70px;
}

.hit-points-bar-wrapper {
  min-width: 200px;
}

.jet-bar-wrapper {
  min-width: 200px;
}
</style>
