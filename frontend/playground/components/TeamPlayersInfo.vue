<script setup lang="ts">
import {computed} from "vue";
import type {PlayerInfo} from "~/playground/data/common";

const props = defineProps<{
  teamNumber: number
  teamPlayers: PlayerInfo[]
  hideTotals?: boolean
}>()

const teamTotals = computed<PlayerInfo>(() => {
  return props.teamPlayers.reduce(
      (totals, player) => ({
        nickname: 'Team' + props.teamNumber,
        color: props.teamNumber % 2 == 0 ? 'blue' : 'red',
        hp: totals.hp + player.hp,
        maxHp: totals.maxHp + player.maxHp,
        frags: totals.frags + player.frags,
        damage: totals.damage + player.damage
      }),
      { nickname: 'Team' + props.teamNumber, color: 'gray', hp: 0, maxHp: 0, frags: 0, damage: 0 }
  )
})

const allInfo = computed<PlayerInfo[]>(() => {
  return props.hideTotals ? props.teamPlayers : [teamTotals.value, ...props.teamPlayers]
})
</script>

<template>
  <v-table class="team-table" density="compact">
    <thead>
    <tr>
      <th colspan="2">Hit points</th>
      <th>Frags</th>
      <th>Damage</th>
    </tr>
    </thead>
    <tbody>
    <tr v-for="player in allInfo" :key="player.nickname">
      <td class="progress-cell">
        <v-progress-linear
            bg-color="blue-grey"
            height="16"
            :color="player.color"
            :model-value="Math.floor(100 * player.hp / player.maxHp)"
        >
          <span class="progress-text">{{ player.nickname }}: {{ player.hp }}</span>
        </v-progress-linear>
      </td>
      <td></td>
      <td class="text-center">{{ player.frags }}</td>
      <td class="text-center">{{ player.damage }}</td>
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

.team-header {
  text-align: center !important;
  font-size: 18px;
  font-weight: bold;
  padding: 8px 16px;
  background-color: rgba(255, 255, 255, 0.1) !important;
}

.progress-cell {
  min-width: 200px;
  padding: 4px 8px !important;
}

.progress-cell :deep(.v-progress-linear) {
  border-radius: 4px;
  overflow: hidden;
}

.progress-text {
  font-size: 12px;
  font-weight: bold;
  color: white;
  text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.8);
  padding: 0 4px;
  white-space: nowrap;
}

.text-center {
  text-align: center;
}

/* Make sure the tables are transparent */
:deep(.v-table) {
  background: transparent !important;
}

:deep(.v-table__wrapper) {
  background: transparent !important;
}
</style>
