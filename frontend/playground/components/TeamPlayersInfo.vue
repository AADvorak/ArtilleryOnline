<script setup lang="ts">
import {computed} from "vue";
import type {PlayerInfo} from "~/playground/data/common";
import BattleLinearProgress from "~/playground/components/BattleLinearProgress.vue";
import {DefaultColors} from "~/dictionary/default-colors";
import {useI18n} from "vue-i18n";

const props = defineProps<{
  usersTeamId: number
  teamId: number
  teamPlayers: PlayerInfo[]
  showTotals?: boolean
  showDetails?: boolean
}>()

const {t} = useI18n()

const teamTotals = computed<PlayerInfo>(() => {
  const nickname = 'Team' + (props.teamId + 1)
  const color = props.teamId === props.usersTeamId ? DefaultColors.ALLY_TEAM : DefaultColors.ENEMY_TEAM
  return props.teamPlayers.reduce(
      (totals, player) => ({
        nickname,
        color,
        hp: totals.hp + player.hp,
        maxHp: totals.maxHp + player.maxHp,
        frags: totals.frags + player.frags,
        damage: totals.damage + player.damage
      }),
      { nickname, color, hp: 0, maxHp: 0, frags: 0, damage: 0 }
  )
})

const allInfo = computed<PlayerInfo[]>(() => {
  const output: PlayerInfo[] = []
  if (props.showTotals) {
    output.push(teamTotals.value)
  }
  if (props.showDetails) props.teamPlayers.forEach(info => output.push(info))
  return output
})
</script>

<template>
  <v-table class="team-table" density="compact">
    <thead v-show="showDetails">
    <tr>
      <th>{{ t('battleHeader.hitPoints') }}</th>
      <th>{{ t('battleHeader.frags') }}</th>
      <th>{{ t('battleHeader.damage') }}</th>
    </tr>
    </thead>
    <tbody>
    <tr v-for="player in allInfo" :key="player.nickname">
      <td class="progress-cell">
        <battle-linear-progress
            :value="Math.floor(100 * player.hp / player.maxHp)"
            :text="player.nickname + ': ' + Math.floor(player.hp)"
            :color="player.color"
        />
      </td>
      <td class="text-right">{{ player.frags }}</td>
      <td class="text-right">{{ player.damage }}</td>
    </tr>
    </tbody>
  </v-table>
</template>

<style scoped>
.team-table {
  min-width: 300px;
  background: transparent !important;
}

.team-table :deep(.v-table__wrapper) {
  background: transparent !important;
}

.team-table :deep(th),
.team-table :deep(td) {
  background: transparent !important;
  color: white !important;
  border-bottom: 1px solid rgba(255, 255, 255, 0.2) !important;
}

.progress-cell {
  min-width: 250px;
  padding: 4px 8px !important;
}

.text-right {
  text-align: right;
}

/* Make sure the tables are transparent */
:deep(.v-table) {
  background: transparent !important;
}

:deep(.v-table__wrapper) {
  background: transparent !important;
}
</style>
