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

const droneState = computed(() => {
  return userVehicle.value?.state.droneState
})
</script>

<template>
  <v-btn v-if="droneState && !droneState.launched"
      class="drone-btn"
      :color="droneState.readyToLaunch ? 'success' : 'warning'"
  >
    {{ t('battleHeader.drone') }}: {{ droneState.readyToLaunch ? t('battleHeader.ready') : t('battleHeader.preparing') }}
  </v-btn>
</template>

<style scoped>
.drone-btn {
  padding: 0 8px;
}
</style>
