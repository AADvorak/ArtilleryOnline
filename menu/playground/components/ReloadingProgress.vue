<script setup lang="ts">
import { computed } from 'vue'
import { useBattleStore } from '@/playground/stores/battle'
import type {StompClient} from "@/playground/composables/stomp-client";
import {useCommandsSender} from "@/playground/composables/commands-sender";
import {Command} from "@/playground/data/command";
import {useUserKeyStore} from "~/playground/stores/user-key";

const props = defineProps<{
  stompClient: StompClient
}>()

const userStore = useUserKeyStore()
const battleStore = useBattleStore()
const commandsSender = useCommandsSender(props.stompClient)

const userVehicle = computed(() => {
  if (!userStore.userKey) {
    return null
  }
  return battleStore.battle?.model.vehicles[userStore.userKey]
})

const ammo = computed(() => {
  return userVehicle.value?.state.ammo
})

const selectedShell = computed(() => {
  return userVehicle.value?.state.gunState.selectedShell
})

const ammoKeys = computed(() => {
  return Object.keys(ammo.value)
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

function selectShell(key) {
  if (key !== selectedShell.value) {
    commandsSender.sendCommand({
      command: Command.SELECT_SHELL,
      params: {shellType: key}
    })
  }
}
</script>

<template>
  <v-progress-circular v-if="showProgress" color="lime" :model-value="reloadingProgress" />
  <template v-for="ammoKey in ammoKeys">
    <v-btn
        :color="ammoKey === selectedShell ? 'primary' : ''"
        @click="() => selectShell(ammoKey)"
    >
      {{ ammoKey }}: {{ ammo[ammoKey] }}
    </v-btn>
  </template>
</template>
