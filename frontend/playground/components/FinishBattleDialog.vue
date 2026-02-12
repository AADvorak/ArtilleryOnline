<script setup lang="ts">
import {computed, ref, watch} from 'vue'
import {useBattleStore} from '~/stores/battle'
import {BattleStage, BattleType} from '@/playground/data/battle'
import {useI18n} from "vue-i18n";
import {useUserStore} from "~/stores/user";
import {DefaultColors} from "~/dictionary/default-colors";

enum BattleResult {
  DRAW = 'draw',
  VICTORY = 'victory',
  DEFEAT = 'defeat',
}

const {t} = useI18n()
const battleStore = useBattleStore()

const userStore = useUserStore()

const battle = computed(() => battleStore.battle)

const title = computed(() => {
  const battleStage = battle.value?.battleStage
  if (battleStage === BattleStage.FINISHED) {
    return t('finishBattleDialog.title')
  }
  if (battleStage === BattleStage.ERROR) {
    return t('common.error')
  }
  return ''
})

const message = computed(() => {
  const battleStage = battle.value?.battleStage
  if (battleStage === BattleStage.FINISHED) {
    return t('finishBattleDialog.message')
  }
  if (battleStage === BattleStage.ERROR) {
    return t('finishBattleDialog.errorMessage')
  }
  return ''
})

const usersTeamId = computed<number>(() => {
  return battleStore.battle?.nicknameTeamMap[userStore.user!.nickname] || 0
})

const battleResult = computed(() => {
  if (battle.value?.type === BattleType.TEAM_ELIMINATION) {
    if (battle.value.winnerTeamId !== undefined) {
      return usersTeamId.value === battle.value.winnerTeamId ? BattleResult.VICTORY : BattleResult.DEFEAT
    }
    return BattleResult.DRAW
  }
})

const battleResultColor = computed(() => {
  switch (battleResult.value) {
    case BattleResult.VICTORY:
      return DefaultColors.BRIGHT_GREEN
    case BattleResult.DEFEAT:
      return DefaultColors.BRIGHT_RED
    case BattleResult.DRAW:
      return DefaultColors.BRIGHT_ORANGE
  }
})

const cardColor = computed(() => {
  switch (battleResult.value) {
    case BattleResult.VICTORY:
      return DefaultColors.BRIGHT_GREEN_BG
    case BattleResult.DEFEAT:
      return DefaultColors.BRIGHT_RED_BG
    case BattleResult.DRAW:
      return DefaultColors.BRIGHT_ORANGE_BG
  }
})

const opened = ref(false)

watch(battle, (value) => {
  const battleStage = value?.battleStage
  if (battleStage === BattleStage.FINISHED) {
    setTimeout(() => opened.value = true, 2000)
  }
  if (battleStage === BattleStage.ERROR) {
    opened.value = true
  }
})

function hideAndCleanBattle() {
  opened.value = false
  battleStore.clear()
}
</script>

<template>
  <v-dialog :model-value="opened" :persistent="true" max-width="600px">
    <v-card :color="cardColor" width="100%">
      <v-card-title>{{ title }}</v-card-title>
      <v-card-text>
        <div class="d-flex">{{ message }}</div>
        <div v-if="battleResult">
          {{ t('commonHistory.battleResult') }}:&nbsp;
          <span :style="'color: ' + battleResultColor">
            {{ t('commonHistory.battleResults.' + battleResult) }}
          </span>
        </div>
        <div class="d-flex mt-4">
          <v-btn color="primary" @click="hideAndCleanBattle">{{ t('common.ok') }}</v-btn>
        </div>
      </v-card-text>
    </v-card>
  </v-dialog>
</template>
