<script setup lang="ts">
import { computed } from 'vue'
import { useBattleStore } from '~/stores/battle'
import {useUserStore} from '~/stores/user'
import {useI18n} from "vue-i18n";
import BattleLinearProgress from "~/playground/components/BattleLinearProgress.vue";
import {DefaultColors} from "~/dictionary/default-colors";

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
  return value * 100
})

const progressColor = computed(() => {
  return progress.value === 100 ? DefaultColors.PROGRESS_READY : DefaultColors.PROGRESS_NOT_READY
})
</script>

<template>
  <div v-if="bomberState" class="progress-wrapper ml-4">
    <battle-linear-progress
        :value="progress"
        :text="t('battleHeader.bomber') + ': ' + bomberState.remainFlights"
        :color="progressColor"
    />
  </div>
</template>

<style scoped>
.progress-wrapper {
  min-width: 100px;
}
</style>
