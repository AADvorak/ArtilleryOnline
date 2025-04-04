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
</script>

<template>
  <v-btn v-if="bomberState && bomberState.remainFlights > 0"
      class="bomber-btn"
      :color="bomberState.readyToFlight ? 'success' : 'warning'"
  >
    {{ t('battleHeader.bomber') }}:
    {{ bomberState.readyToFlight ? t('battleHeader.ready') : t('battleHeader.preparing') }},
    {{ t('battleHeader.flights') }}: {{ bomberState.remainFlights }}
  </v-btn>
</template>

<style scoped>
.bomber-btn {
  padding: 0 8px;
}
</style>
