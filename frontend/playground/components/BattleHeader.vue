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
import {mdiCloseThick, mdiHelp, mdiMenu} from '@mdi/js'
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
import {VerticalTooltipLocation} from "~/data/model";
import FullScreenBtn from "~/components/full-screen-btn.vue";
import {useRouter} from "#app";

const RESERVED_WIDTH = 416
const HP_BAR_WIDTH = 216

const {t} = useI18n()
const battleStore = useBattleStore()
const userStore = useUserStore()
const settingsStore = useSettingsStore()
const userSettingsStore = useUserSettingsStore()
const globalStateStore = useGlobalStateStore()
const router = useRouter()

const props = defineProps<{
  showControlButtons: boolean
}>()

const availableHpSlots = ref<number>(0)

const leaveBattleDialog = ref<InstanceType<typeof LeaveBattleDialog> | null>(null)
const helpDialog = ref<InstanceType<typeof HelpDialog> | null>(null)

const appearances = computed(() => userSettingsStore.appearancesOrDefaultsNameValueMapping)

const userKeys = computed(() => {
  if (!availableHpSlots.value) {
    return []
  }
  const keys = Object.keys(battleStore.vehicles || [])
  if (appearances.value[AppearancesNames.ALL_HP_TOP] !== '1' || keys.length > 2 * availableHpSlots.value) {
    return [userStore.user!.nickname]
  }
  return keys
})

const userKeyPairs = computed(() => {
  const usersCount = userKeys.value.length
  const pairs = []
  if (usersCount === 0) {
    return pairs
  }
  if (usersCount <= availableHpSlots.value) {
    for (const userKey of userKeys.value) {
      pairs.push([userKey])
    }
  } else {
    const pairsCount = Math.ceil(usersCount / 2)
    for (let pairNumber = 0; pairNumber < pairsCount; pairNumber++) {
      const pair = []
      const first = userKeys.value[2 * pairNumber]
      first && pair.push(first)
      const second = userKeys.value[2 * pairNumber + 1]
      second && pair.push(second)
      pairs.push(pair)
    }
  }
  return pairs
})

const jetAvailable = computed(() => {
  const vehicle = battleStore.battle?.model.vehicles[userStore.user!.nickname]
  return !!vehicle && !!vehicle.config.jet
})

const isDebugMode = computed(() => settingsStore.settings?.debug)

onMounted(() => {
  calculateAvailableHpSlots()
  addEventListener('resize', calculateAvailableHpSlots)
  addEventListener('keyup', showHelpIfF1Pressed)
})

onUnmounted(() => {
  removeEventListener('resize', calculateAvailableHpSlots)
  removeEventListener('keyup', showHelpIfF1Pressed)
})

function showLeaveBattleDialog() {
  leaveBattleDialog.value?.show()
}

function showHelpIfF1Pressed(e) {
  if (e.code === 'F1') {
    showHelp()
  }
}

function showHelp() {
  if (props.showControlButtons && !globalStateStore.showHelp) {
    globalStateStore.showHelp = VerticalTooltipLocation.TOP
    setTimeout(() => globalStateStore.showHelp = VerticalTooltipLocation.BOTTOM, 3000)
    setTimeout(() => globalStateStore.showHelp = undefined, 6000)
  } else {
    helpDialog.value?.show()
  }
}

function toMenu() {
  router.push('/menu')
}

function calculateAvailableHpSlots() {
  let availableWidth = window.innerWidth - RESERVED_WIDTH
  if (availableWidth < 0) availableWidth = 0
  availableHpSlots.value = Math.floor(availableWidth / HP_BAR_WIDTH)
}
</script>

<template>
  <v-toolbar height="36px" color="transparent" class="toolbar">
    <div v-if="isDebugMode" class="ml-4">
      <BattleDebugButtons />
    </div>
    <div class="ml-4 battle-timer-wrapper">
      <BattleTimer />
    </div>
    <div class="ml-2 battle-fps-wrapper">
      <BattleFps />
    </div>
    <div class="ml-2 battle-ping-wrapper">
      <BattlePing />
    </div>
    <div class="ml-4 hit-points-bar-wrapper" v-for="userKeyPair in userKeyPairs">
      <HitPointsBar v-for="userKey in userKeyPair" :user-key="userKey" />
    </div>
    <v-spacer/>
    <icon-btn
        :icon="mdiHelp"
        :tooltip="t('common.help')"
        @click="showHelp"
    />
    <messages-menu/>
    <full-screen-btn />
    <icon-btn
        :icon="mdiMenu"
        :tooltip="t('menu.title')"
        @click="toMenu"
    />
    <icon-btn
        :icon="mdiCloseThick"
        :tooltip="t('battleHeader.leaveBattle')"
        @click="showLeaveBattleDialog"
    />
    <LeaveBattleDialog ref="leaveBattleDialog"/>
    <HelpDialog ref="helpDialog"/>
  </v-toolbar>
  <v-toolbar height="36px" color="transparent" class="toolbar">
    <div v-if="jetAvailable" class="ml-4 jet-bar-wrapper">
      <JetBar />
    </div>
    <Gun />
    <Missiles />
    <Drone />
    <Bomber />
    <ReloadingProgress />
  </v-toolbar>
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

.toolbar {
  z-index: 10;
}
</style>
