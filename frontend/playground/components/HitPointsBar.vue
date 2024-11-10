<script setup lang="ts">
import {useBattleStore} from "~/stores/battle";
import {computed} from "vue";
import {VehicleUtils} from "~/playground/utils/vehicle-utils";

const battleStore = useBattleStore()

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
  if (vehicle.value) {
    return VehicleUtils.getColor(props.userKey, vehicle.value)
  }
  return ''
})

const userKeyFixLength = computed(() => {
  return props.userKey.substring(0, 14)
})
</script>

<template>
  <v-progress-linear
      bg-color="blue-grey"
      height="16"
      :color="color"
      :model-value="value"
  >
    <span class="progress-text">{{ userKeyFixLength }}: {{ userHp }} HP</span>
  </v-progress-linear>
</template>

<style>
.progress-text {
  font-size: 16px;
}
</style>
