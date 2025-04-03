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

const gunState = computed(() => {
  return userVehicle.value?.state.gunState
})
</script>

<template>
  <v-btn
      v-if="!!gunState"
      class="ml-5 gun-btn"
  >
    {{ t('battleHeader.gun') }}: {{ gunState.fixed ? t('battleHeader.fixed') : t('battleHeader.autoAngle') }}
  </v-btn>
</template>

<style scoped>
.gun-btn {
  padding: 0 8px;
}
</style>
