<script setup lang="ts">
import {useBattleStore} from "~/stores/battle";
import {useUserStore} from "~/stores/user";
import {useI18n} from "vue-i18n";

const {t} = useI18n()
const battleStore = useBattleStore()
const userStore = useUserStore()

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
  <v-progress-linear
      bg-color="blue-grey"
      height="16"
      color="orange"
      :model-value="value"
  >
    <span class="progress-text">{{ t('jetBar.title') }}</span>
  </v-progress-linear>
</template>

<style>
.progress-text {
  font-size: 16px;
}
</style>
