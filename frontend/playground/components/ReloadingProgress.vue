<script setup lang="ts">
import {computed} from 'vue'
import {useBattleStore} from '~/stores/battle'
import {useCommandsSender} from "@/playground/composables/commands-sender";
import {Command} from "@/playground/data/command";
import {useUserStore} from "~/stores/user";
import {useGlobalStateStore} from "~/stores/global-state";
import VerticalTooltip from "~/components/vertical-tooltip.vue";
import {useI18n} from "vue-i18n";
import {VerticalTooltipLocation} from "~/data/model";
import BattleLinearProgress from "~/playground/components/BattleLinearProgress.vue";
import {DefaultColors} from "~/dictionary/default-colors";

const {t} = useI18n()

const userStore = useUserStore()
const battleStore = useBattleStore()
const globalStateStore = useGlobalStateStore()
const commandsSender = useCommandsSender()

const userVehicle = computed(() => {
  return battleStore.battle?.model.vehicles[userStore.user!.nickname]
})

const ammo = computed(() => {
  return userVehicle.value?.state.ammo
})

const ammoConfig = computed(() => {
  return userVehicle.value?.config.ammo
})

const selectedShell = computed(() => {
  return userVehicle.value?.state.gunState.selectedShell
})

const ammoKeys = computed(() => {
  return (ammoConfig.value || []).map(item => item.name)
})

const reloadingProgress = computed(() => {
  if (!userVehicle.value) {
    return 0
  }
  const loadTime = userVehicle.value.config.gun.loadTime
  const loadRemainTime = userVehicle.value.state.gunState.loadRemainTime
  return Math.floor((100 * (loadTime - loadRemainTime)) / loadTime)
})

const progressColor = computed(() => {
  return reloadingProgress.value === 100 ? DefaultColors.PROGRESS_READY : DefaultColors.PROGRESS_NOT_READY
})

onMounted(() => {
  addEventListener('keyup', keyPressed)
})

onBeforeUnmount(() => {
  removeEventListener('keyup', keyPressed)
})

function keyPressed(e) {
  if (e.code.startsWith('Digit')) {
    const number = parseInt(e.key)
    const ammoKey = ammoKeys.value[number - 1]
    if (ammoKey) {
      selectShell(ammoKey)
    }
  }
}

function selectShell(key: string) {
  if (key !== selectedShell.value) {
    commandsSender.sendCommand({
      command: Command.SELECT_SHELL,
      params: {shellName: key}
    })
  }
}

function getAmmoText(ammoKey: string, index: number) {
  const localeKey = `names.shells.${ammoKey}`
  return `[${index + 1}] ${t(localeKey)}: ${ammo.value ? ammo.value[ammoKey] : 0}`
}
</script>

<template>
  <template v-for="(ammoKey, index) in ammoKeys">
    <div :class="'progress-wrapper ml-' + (index === 0 ? '4' : '2')" >
      <battle-linear-progress
          :value="ammoKey === selectedShell ? reloadingProgress : 0"
          :text="getAmmoText(ammoKey, index)"
          :color="progressColor"
          :clickable="!!(ammo && ammo[ammoKey])"
          @click="() => selectShell(ammoKey)"
      >
        <vertical-tooltip
            :location="VerticalTooltipLocation.BOTTOM"
            :tooltip="ammoKey === selectedShell ? t('controls.selectedShell') : t('controls.selectShell')"
            :show="globalStateStore.showHelp === VerticalTooltipLocation.BOTTOM"
        />
      </battle-linear-progress>
    </div>
  </template>
</template>

<style scoped>
.progress-wrapper {
  min-width: 110px;
}
</style>
