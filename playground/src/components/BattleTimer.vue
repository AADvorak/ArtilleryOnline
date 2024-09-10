<script setup lang="ts">
import {useBattleStore} from "@/stores/battle";
import {computed} from "vue";
import {BattleStage} from "@/data/battle";

const WAITING_STAGE_OFFSET = 15 * 1000
const ACTIVE_STAGE_OFFSET = WAITING_STAGE_OFFSET + 10 * 60 * 1000

const MS_IN_MINUTE = 1000 * 60
const MS_IN_SECOND = 1000

const battleStore = useBattleStore()

const leftTime = computed(() => {
  const battle = battleStore.battle
  if (!battle) {
    return 0
  }
  switch (battle.battleStage) {
    case BattleStage.WAITING:
      return WAITING_STAGE_OFFSET - battle.time
    case BattleStage.ACTIVE:
      return ACTIVE_STAGE_OFFSET - battle.time
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
  return `${formatTwoDigits(minutes)}:${formatTwoDigits(seconds)}`
}

function formatTwoDigits(value: number) {
  return (value < 10 ? '0' : '') + value
}
</script>

<template>
  <div :class="timerClass">{{ leftTimeFormatted }}</div>
</template>

<style scoped>
.battle-timer {
  color: aquamarine;
  font-size: large;
}

.battle-timer_waiting {
  color: crimson;
  font-size: large;
}
</style>
