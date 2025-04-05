<script setup lang="ts">
import { computed } from 'vue'
import { useBattleStore } from '~/stores/battle'
import {useUserStore} from '~/stores/user'
import {useCommandsSender} from "~/playground/composables/commands-sender";
import {Command} from "~/playground/data/command";

const commandsSender = useCommandsSender()

const userStore = useUserStore()
const battleStore = useBattleStore()

const userVehicle = computed(() => {
  return battleStore.battle?.model.vehicles[userStore.user!.nickname]
})

const missiles = computed(() => {
  return userVehicle.value?.state.missiles
})

const missileKeys = computed(() => {
  return Object.keys(missiles.value || {})
})

function launch() {
  commandsSender.sendCommand({
    command: Command.LAUNCH_MISSILE
  })
}
</script>

<template>
  <template v-for="missileKey in missileKeys">
    <v-btn
        class="missile-btn"
        color="primary"
        :disabled="!missiles[missileKey]"
        @click="launch"
    >
      {{ missileKey }}: {{ missiles[missileKey] }}
    </v-btn>
  </template>
</template>

<style scoped>
.missile-btn {
  padding: 0 8px;
}
</style>
