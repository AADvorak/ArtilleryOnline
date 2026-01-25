<script setup lang="ts">
import { computed } from 'vue'
import { useBattleStore } from '~/stores/battle'
import {useUserStore} from '~/stores/user'
import {useI18n} from "vue-i18n";

const {t} = useI18n()

const userStore = useUserStore()
const battleStore = useBattleStore()

const userVehicle = computed(() => {
  return battleStore.battle?.model.vehicles[userStore.user!.nickname]
})

const bomberState = computed(() => {
  return userVehicle.value?.state.bomberState
})

const bomberSpecs = computed(() => {
  return userVehicle.value?.config.bomber
})

const progress = computed(() => {
  let value = 0
  const state = bomberState.value
  const specs = bomberSpecs.value
  if (state && specs) {
    if (state.prepareToFlightRemainTime <= 0) {
      value = 1
    } else if (state.prepareToFlightRemainTime < specs.prepareToFlightTime) {
      value = (specs.prepareToFlightTime - state.prepareToFlightRemainTime) / specs.prepareToFlightTime
    }
  }
  return Math.floor(value * 100)
})
</script>

<template>
  <div v-if="bomberState" class="progress-wrapper">
    <v-progress-linear
        bg-color="blue-grey"
        height="16"
        color="blue"
        class="progress"
        :model-value="progress"
        :disabled="!bomberState.remainFlights"
    >
      <span class="progress-text">{{ t('battleHeader.bomber') }}: {{ bomberState.remainFlights }}</span>
    </v-progress-linear>
  </div>
</template>

<style scoped>
.progress-wrapper {
  min-width: 100px;
}
.progress {
  cursor: pointer;
}
.progress-text {
  font-size: 16px;
}
</style>
