import { computed, ref } from 'vue'
import { defineStore } from 'pinia'
import type { Battle } from '~/playground/data/battle'
import { BattleStage } from '~/playground/data/battle'
import {ApiRequestSender} from "~/api/api-request-sender";
import {deserializeBattle} from "~/playground/data/battle-deserialize";
import {DeserializerInput} from "~/deserialization/deserializer-input";
import type {ParticleModels} from "~/playground/data/model";
import type {ParticleState} from "~/playground/data/state";
import type {ParticleConfig} from "~/playground/data/config";

export const useBattleStore = defineStore('battle', () => {
  const clientBattle = ref<Battle>()

  const serverBattle = ref<Battle>()

  const particles = ref<ParticleModels>({})

  const currentId = ref<number>(0)

  const paused = ref<boolean>(false)

  const doStep = ref<boolean>(false)

  const showServerState = ref<boolean>(false)

  const updateTime = ref<number>()

  const needSmoothTransition = ref<boolean>(false)

  const battle = computed(() => {
    if (showServerState.value) {
      return serverBattle.value
    }
    return clientBattle.value || serverBattle.value
  })

  const vehicles = computed(() => battle.value?.model?.vehicles)

  const shells = computed(() => battle.value?.model?.shells)

  const missiles = computed(() => battle.value?.model?.missiles)

  const drones = computed(() => battle.value?.model?.drones)

  const explosions = computed(() => battle.value?.model?.explosions)

  const isActive = computed(() => battle.value?.battleStage === BattleStage.ACTIVE)

  async function loadBattleIfNull() {
    if (!battle.value) {
      await loadBattle()
    }
  }

  async function loadBattle() {
    try {
      const battleBinary = await new ApiRequestSender().getBytes('/battles')
      const battle = deserializeBattle(new DeserializerInput(battleBinary))
      updateBattle(battle)
    } catch (e) {
      console.log(e)
    }
  }

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

  function addParticle(state: ParticleState, config: ParticleConfig) {
    const id = currentId.value++
    particles.value[id] = {id, state, config}
  }

  function removeParticle(id: number) {
    delete particles.value[id]
  }

  function clear() {
    clientBattle.value = undefined
    serverBattle.value = undefined
    currentId.value = 0
    particles.value = {}
  }

  return {
    battle,
    clientBattle,
    serverBattle,
    vehicles,
    shells,
    missiles,
    drones,
    explosions,
    particles,
    isActive,
    loadBattleIfNull,
    loadBattle,
    updateBattle,
    updateClientBattle,
    updateServerBattle,
    addParticle,
    removeParticle,
    clear,
    paused,
    doStep,
    showServerState,
    updateTime,
    needSmoothTransition,
  }
})
