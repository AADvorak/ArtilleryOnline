<script setup lang="ts">
import { computed } from 'vue'
import { useBattleStore } from '~/stores/battle'
import {useUserStore} from '~/stores/user'
import {useI18n} from "vue-i18n";
import {mdiCrosshairs, mdiCrosshairsOff} from "@mdi/js";

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
  <icon-btn
      class="ml-2"
      v-if="!!gunState"
      :icon="gunState.fixed ? mdiCrosshairsOff : mdiCrosshairs"
      :tooltip="t('battleHeader.gun') + ': ' + (gunState.fixed ? t('battleHeader.fixed') : t('battleHeader.autoAngle'))"
  />
</template>
