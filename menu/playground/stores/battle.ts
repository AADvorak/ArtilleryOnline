import { computed, ref } from 'vue'
import { defineStore } from 'pinia'
import type { Battle } from '@/playground/data/battle'
import { BattleStage } from '@/playground/data/battle'

export const useBattleStore = defineStore('battle', () => {
  const clientBattle = ref<Battle>()

  const serverBattle = ref<Battle>()

  const battle = computed(() => {
    if (showServerState.value) {
      return serverBattle.value
    }
    return clientBattle.value || serverBattle.value
  })

  const paused = ref<boolean>(false)

  const doStep = ref<boolean>(false)

  const showServerState = ref<boolean>(false)

  const updateTime = ref<number>()

  const vehicles = computed(() => battle.value?.model?.vehicles)

  const shells = computed(() => battle.value?.model?.shells)

  const explosions = computed(() => battle.value?.model?.explosions)

  const isActive = computed(() => battle.value?.battleStage === BattleStage.ACTIVE)

  function updateBattle(value: Battle, time?: number) {
    clientBattle.value = value
    serverBattle.value = value
    paused.value = value.paused
    updateTime.value = time ? time : new Date().getTime()
  }

  function updateClientBattle(value: Battle, time?: number) {
    clientBattle.value = value
    updateTime.value = time ? time : new Date().getTime()
  }

  function updateServerBattle(value: Battle, time?: number) {
    serverBattle.value = value
    paused.value = value.paused
    updateTime.value = time ? time : new Date().getTime()
  }

  function clear() {
    clientBattle.value = undefined
    serverBattle.value = undefined
  }

  return {
    battle,
    vehicles,
    shells,
    explosions,
    isActive,
    updateBattle,
    updateClientBattle,
    updateServerBattle,
    clear,
    paused,
    doStep,
    showServerState,
    updateTime
  }
})
