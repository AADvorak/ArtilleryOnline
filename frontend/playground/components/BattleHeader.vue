<script setup lang="ts">
import {computed} from "vue";
import {useBattleStore} from "~/stores/battle";
import ReloadingProgress from "@/playground/components/ReloadingProgress.vue";
import BattleTimer from "@/playground/components/BattleTimer.vue";
import BattleDebugButtons from "@/playground/components/BattleDebugButtons.vue";
import {useSettingsStore} from "~/stores/settings";
import JetBar from "~/playground/components/JetBar.vue";
import LeaveBattleDialog from "~/playground/components/LeaveBattleDialog.vue";
import {useUserStore} from "~/stores/user";
import {mdiCloseThick, mdiHelp, mdiMenu} from '@mdi/js'
import IconBtn from "~/components/icon-btn.vue";
import HelpDialog from "~/playground/components/HelpDialog.vue";
import {useI18n} from "vue-i18n";
import BattleFps from "~/playground/components/BattleFps.vue";
import BattlePing from "~/playground/components/BattlePing.vue";
import Missiles from "~/playground/components/Missiles.vue";
import Drones from "~/playground/components/Drones.vue";
import Bomber from "~/playground/components/Bomber.vue";
import Gun from "~/playground/components/Gun.vue";
import {useGlobalStateStore} from "~/stores/global-state";
import {VerticalTooltipLocation} from "~/data/model";
import FullScreenBtn from "~/components/full-screen-btn.vue";
import {useRouter} from "#app";
import {BattleType} from "~/playground/data/battle";
import PlayersInfo from "~/playground/components/PlayersInfo.vue";

const {t} = useI18n()
const battleStore = useBattleStore()
const userStore = useUserStore()
const settingsStore = useSettingsStore()
const globalStateStore = useGlobalStateStore()
const router = useRouter()

const props = defineProps<{
  showControlButtons: boolean
  separateHeaderToolbars: boolean
}>()

const leaveBattleDialog = ref<InstanceType<typeof LeaveBattleDialog> | null>(null)
const helpDialog = ref<InstanceType<typeof HelpDialog> | null>(null)

const isDebugMode = computed(() => settingsStore.settings?.debug)

const centerToolbarClass = computed(() => {
  return 'toolbar center-top-toolbar ' + (props.separateHeaderToolbars ? 'top-toolbar-separate' : 'top-toolbar-merged')
})

onMounted(() => {
  addEventListener('keyup', showHelpIfF1Pressed)
})

onUnmounted(() => {
  removeEventListener('keyup', showHelpIfF1Pressed)
})

function leaveBattle() {
  const battleType = battleStore.battle?.type
  const withoutDialog = battleType && [BattleType.COLLIDER, BattleType.TEST_DRIVE].includes(battleType)
  withoutDialog ? leaveBattleDialog.value?.leaveBattle() : leaveBattleDialog.value?.show()
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
</script>

<template>
  <v-toolbar
      height="36px"
      color="transparent"
      class="toolbar"
  >
    <div class="ml-4 battle-timer-wrapper">
      <BattleTimer />
    </div>
    <div class="ml-2 battle-fps-wrapper">
      <BattleFps />
    </div>
    <div class="ml-2 battle-ping-wrapper">
      <BattlePing />
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
        @click="leaveBattle"
    />
  </v-toolbar>
  <v-toolbar
      absolute
      height="36px"
      color="transparent"
      :class="centerToolbarClass"
  >
    <Gun />
    <JetBar />
    <Missiles />
    <Drones />
    <Bomber />
    <ReloadingProgress />
  </v-toolbar>
  <v-toolbar
      v-if="isDebugMode"
      absolute
      height="36px"
      color="transparent"
      class="toolbar left-top-toolbar top-toolbar-separate"
  >
    <BattleDebugButtons />
  </v-toolbar>
  <PlayersInfo :separate-header-toolbars="separateHeaderToolbars" />
  <HelpDialog ref="helpDialog"/>
  <LeaveBattleDialog ref="leaveBattleDialog"/>
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

.left-top-toolbar {
  top: 0;
  left: 0;
  width: auto;
}

.center-top-toolbar {
  left: 50%;
  transform: translateX(-50%);
  width: auto;
}

.top-toolbar-merged {
  top: 0
}

.top-toolbar-separate {
  top: 36px
}

.toolbar {
  z-index: 10;
}
</style>
