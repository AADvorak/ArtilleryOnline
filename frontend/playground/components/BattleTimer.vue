<script setup lang="ts">
import {useBattleStore} from "~/stores/battle";
import {computed} from "vue";
import {BattleStage, BattleType} from "@/playground/data/battle";
import {useI18n} from "vue-i18n";

const MS_IN_MINUTE = 1000 * 60
const MS_IN_SECOND = 1000

const WAITING_STAGE_LENGTH = 5 * MS_IN_SECOND

const SHOW_HINT_BATTLE_TYPES = [
  BattleType.RANDOM,
  BattleType.DRONE_HUNT,
  BattleType.DEATHMATCH,
  BattleType.TEAM_CONTROL,
  BattleType.TEAM_ELIMINATION
]

const {t} = useI18n()

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
      return battle.duration - battle.time
    default:
      return 0
  }
})

const isWaitingStage = computed(() => battleStore.battle?.battleStage === BattleStage.WAITING)

const timerClass = computed(() => {
  return isWaitingStage.value ? 'battle-timer_waiting' : 'battle-timer'
})

const leftTimeFormatted = computed(() => {
  return millisecondsToTime(leftTime.value)
})

const timerText = computed(() => {
  const key: string = isWaitingStage.value
      ? 'battleTimer.battleStartsIn'
      : 'battleTimer.battleEndsIn'
  return t(key)
})

const hintText = computed(() => {
  const battleType = battleStore.battle?.type
  if (battleType && SHOW_HINT_BATTLE_TYPES.includes(battleType)) {
    return t('common.battleHints.' + battleType)
  }
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
  <div v-show="leftTime > 0 && leftTime < WAITING_STAGE_LENGTH" class="centered-timer">
    <div v-if="isWaitingStage" class="hint-text">{{ hintText }}</div>
    <div class="centered-timer-text">{{ timerText }}</div>
    <div class="centered-timer-time">{{ leftTimeFormatted }}</div>
  </div>
</template>

<style scoped>
.battle-timer {
  color: aquamarine;
  font-size: 14px;
  font-weight: bold;
}

.battle-timer_waiting {
  color: crimson;
  font-size: 14px;
  font-weight: bold;
}

.centered-timer {
  position: fixed;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  text-align: center;
  white-space: nowrap;
}

.centered-timer-time {
  font-size: 70px;
  color: crimson;
}

.centered-timer-text {
  font-size: 18px;
  color: crimson;
}

.hint-text {
  font-size: 18px;
  color: aquamarine;
}
</style>
