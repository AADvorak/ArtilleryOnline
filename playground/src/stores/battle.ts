import { computed, ref } from 'vue'
import { defineStore } from 'pinia'
import type { Battle } from '@/data/battle'
import { BattleStage } from '@/data/battle'

export const useBattleStore = defineStore('battle', () => {
  const battle = ref<Battle>()

  const vehicles = computed(() => battle.value?.model?.vehicles)

  const shells = computed(() => battle.value?.model?.shells)

  const explosions = computed(() => battle.value?.model?.explosions)

  const isActive = computed(() => battle.value?.battleStage === BattleStage.ACTIVE)

  return { battle, vehicles, shells, explosions, isActive }
})
