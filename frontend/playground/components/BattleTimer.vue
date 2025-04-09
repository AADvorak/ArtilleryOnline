<script setup lang="ts">
import {useBattleStore} from "~/stores/battle";
import {computed} from "vue";
import {BattleStage} from "@/playground/data/battle";

const MS_IN_MINUTE = 1000 * 60
const MS_IN_SECOND = 1000

const WAITING_STAGE_LENGTH = 5 * MS_IN_SECOND
const ACTIVE_STAGE_LENGTH = 5 * MS_IN_MINUTE

const battleStore = useBattleStore()

const leftTime = computed(() => {
  const battle = battleStore.battle
  if (!battle) {
    return 0
  }
  switch (battle.battleStage) {
    case BattleStage.WAITING:
      return WAITING_STAGE_LENGTH - battle.time
    case BattleStage.ACTIVE:
      return ACTIVE_STAGE_LENGTH - battle.time
    default:
      return 0
  }
})

const timerClass = computed(() => {
  return battleStore.battle?.battleStage === BattleStage.WAITING ? 'battle-timer_waiting' : 'battle-timer'
})

const leftTimeFormatted = computed(() => {
  return millisecondsToTime(leftTime.value)
})

function millisecondsToTime(milliseconds: number) {
  let leftMilliseconds = milliseconds
  const minutes = Math.floor(leftMilliseconds / MS_IN_MINUTE)
  leftMilliseconds -= minutes * MS_IN_MINUTE
  const seconds = Math.floor(leftMilliseconds / MS_IN_SECOND)
  leftMilliseconds -= seconds * MS_IN_SECOND
  return battleStore.paused
      ? `${padStart(minutes)}:${padStart(seconds)}:${padStart(leftMilliseconds, 3)}`
      : `${padStart(minutes)}:${padStart(seconds)}`
}

function padStart(value: number, maxLength: number = 2) {
  return String(value).padStart(maxLength, '0')
}
</script>

<template>
  <div :class="timerClass">{{ leftTimeFormatted }}</div>
</template>

<style scoped>
.battle-timer {
  color: aquamarine;
  font-size: 16px;
}

.battle-timer_waiting {
  color: crimson;
  font-size: 16px;
}
</style>
