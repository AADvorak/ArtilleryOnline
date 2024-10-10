<script setup lang="ts">

import {useBattleStore} from "@/playground/stores/battle";
import {computed} from "vue";
import {useUserKeyStore} from "~/playground/stores/user-key";

const battleStore = useBattleStore()
const userStore = useUserKeyStore()

const props = defineProps<{
  userKey: string
}>()

const vehicle = computed(() => {
  const vehicles = battleStore.vehicles
  return vehicles ? vehicles[props.userKey] : null
})

const value = computed(() => {
  if (!vehicle.value) {
    return 0
  }
  const leftHp = vehicle.value.state.hitPoints
  const maxHp = vehicle.value.specs.hitPoints
  return Math.floor(100 * leftHp / maxHp)
})

const userHp = computed(() => {
  if (!vehicle.value) {
    return 0
  }
  return Math.floor(vehicle.value.state.hitPoints)
})

const color = computed(() => {
  return props.userKey === userStore.userKey ? 'success' : 'error'
})
</script>

<template>
  <v-progress-linear
      bg-color="blue-grey"
      height="25"
      :color="color"
      :model-value="value"
  >
    <span>{{ userKey }}: {{ userHp }} HP</span>
  </v-progress-linear>
</template>
