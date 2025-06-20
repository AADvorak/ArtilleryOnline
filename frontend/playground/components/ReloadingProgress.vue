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

const selectedShell = computed(() => {
  return userVehicle.value?.state.gunState.selectedShell
})

const ammoKeys = computed(() => {
  return Object.keys(ammo.value || {}).sort()
})

const reloadingProgress = computed(() => {
  if (!userVehicle.value) {
    return 0
  }
  const loadTime = userVehicle.value.config.gun.loadTime
  const loadRemainTime = userVehicle.value.state.gunState.loadRemainTime
  return Math.floor((100 * (loadTime - loadRemainTime)) / loadTime)
})

const showProgress = computed(() => {
  if (!userVehicle.value) {
    return false
  }
  return !!userVehicle.value.state.gunState.loadingShell
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

function selectShell(key) {
  if (key !== selectedShell.value) {
    commandsSender.sendCommand({
      command: Command.SELECT_SHELL,
      params: {shellName: key}
    })
  }
}
</script>

<template>
  <template v-for="(ammoKey, index) in ammoKeys">
    <v-btn
        class="ammo-btn"
        :color="ammoKey === selectedShell ? 'primary' : ''"
        :disabled="!ammo[ammoKey]"
        @click="() => selectShell(ammoKey)"
    >
      [{{ index + 1 }}] {{ t(`names.shells.${ammoKey}`) }}: {{ ammo[ammoKey] }}
      <vertical-tooltip
          :location="VerticalTooltipLocation.BOTTOM"
          :tooltip="ammoKey === selectedShell ? t('controls.selectedShell') : t('controls.selectShell')"
          :show="globalStateStore.showHelp === VerticalTooltipLocation.BOTTOM"
      />
    </v-btn>
  </template>
  <v-progress-circular v-if="showProgress" color="lime" :model-value="reloadingProgress" />
</template>

<style scoped>
.ammo-btn {
  padding: 0 8px;
}
</style>
