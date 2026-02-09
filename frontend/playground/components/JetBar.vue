<script setup lang="ts">
import {useBattleStore} from "~/stores/battle";
import {useUserStore} from "~/stores/user";
import {useI18n} from "vue-i18n";
import BattleLinearProgress from "~/playground/components/BattleLinearProgress.vue";

const {t} = useI18n()
const battleStore = useBattleStore()
const userStore = useUserStore()

const jetAvailable = computed(() => {
  const vehicle = battleStore.battle?.model.vehicles[userStore.user!.nickname]
  return !!vehicle && !!vehicle.config.jet
})

const value = computed(() => {
  const vehicle = battleStore.battle?.model.vehicles[userStore.user!.nickname]
  if (!vehicle) {
    return 0
  }
  const volume = vehicle.state.jetState.volume
  const capacity = vehicle.config.jet.capacity
  return 100 * volume / capacity
})
</script>

<template>
  <div v-if="jetAvailable" class="progress-wrapper ml-2">
    <battle-linear-progress
        :value="value"
        :text="t('jetBar.title')"
        color="orange"
    />
  </div>
</template>

<style scoped>
.progress-wrapper {
  min-width: 80px;
}
</style>