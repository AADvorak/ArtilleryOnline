<script setup lang="ts">
import { computed } from 'vue'
import { useBattleStore } from '~/stores/battle'
import {useUserStore} from '~/stores/user'

const userStore = useUserStore()
const battleStore = useBattleStore()

const userVehicle = computed(() => {
  return battleStore.battle?.model.vehicles[userStore.user!.nickname]
})

const droneState = computed(() => {
  return userVehicle.value?.state.droneState
})
</script>

<template>
  <v-btn v-if="droneState && !droneState.launched"
      class="ml-5 drone-btn"
      :color="droneState.readyToLaunch ? 'primary' : ''"
  >
    Drone
  </v-btn>
</template>

<style scoped>
.drone-btn {
  padding: 0 8px;
}
</style>
