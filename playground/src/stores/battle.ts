import { computed, ref } from 'vue'
import { defineStore } from 'pinia'
import type { Battle } from '@/data/battle'
import { BattleStage } from '@/data/battle'

export const useBattleStore = defineStore('battle', () => {
  const battle = ref<Battle>()

  const vehicles = computed(() => battle.value?.model?.vehicles)

  const isActive = computed(() => battle.value?.battleStage === BattleStage.ACTIVE)

  return { battle, vehicles, isActive }
})
