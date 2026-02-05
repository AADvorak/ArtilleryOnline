<template>
  <div v-show="showDetails || isTeamBattle" class="players-info-container">
    <div class="teams-container">
      <team-players-info
          :team-number="1"
          :team-players="team1Players"
          :show-totals="isTeamBattle"
          :show-details="showDetails"
      />
      <template v-if="isTeamBattle">
        <div class="table-spacer"></div>
        <team-players-info
            :team-number="2"
            :team-players="team2Players"
            show-totals
            :show-details="showDetails"
        />
      </template>
    </div>
  </div>
</template>

<script setup lang="ts">
import type {PlayerInfo} from "~/playground/data/common";
import TeamPlayersInfo from "~/playground/components/TeamPlayersInfo.vue";
import {computed} from "vue";
import {useBattleStore} from "~/stores/battle";
import {BattleType} from "~/playground/data/battle";

const battleStore = useBattleStore()

const showDetails = ref(false)

const isTeamBattle = computed(() => battleStore.battle?.type === BattleType.TEAM_ELIMINATION)

const team1Players = computed<PlayerInfo[]>(() => {
  return getTeamNicknames(0).map(nicknameToPlayerInfo)
})

const team2Players = computed<PlayerInfo[]>(() => {
  return getTeamNicknames(1).map(nicknameToPlayerInfo)
})

onMounted(() => {
  addEventListener('keyup', switchShowDetailsIfTabPressed)
})

onUnmounted(() => {
  removeEventListener('keyup', switchShowDetailsIfTabPressed)
})

function switchShowDetailsIfTabPressed(e) {
  if (e.code === 'Tab') {
    showDetails.value = !showDetails.value
  }
}

function getTeamNicknames(teamId: number): string[] {
  const nicknameTeamMap = battleStore.battle?.nicknameTeamMap || {}
  const nicknames: string[] = []
  Object.keys(nicknameTeamMap).forEach(nickname => {
    if (nicknameTeamMap[nickname] === teamId) nicknames.push(nickname)
  })
  return nicknames
}

function nicknameToPlayerInfo(nickname: string): PlayerInfo {
  const vehicle = battleStore.vehicles && battleStore.vehicles[nickname]
  let color = 'white'
  let hp = 0
  let maxHp = 0
  let damage = 0
  let frags = 0
  if (vehicle) {
    hp = vehicle.state.hitPoints
    maxHp = vehicle.specs.hitPoints
    if (vehicle.config.color) color = vehicle.config.color
  }
  return {
    nickname,
    color,
    hp,
    maxHp,
    damage,
    frags
  }
}
</script>

<style scoped>
.players-info-container {
  position: absolute;
  top: 36px;
  left: 50%;
  transform: translateX(-50%);
  z-index: 1000;
  opacity: 0.9;
  pointer-events: none;
}

.teams-container {
  display: flex;
  gap: 32px;
  background-color: rgba(0, 0, 0, 0.6);
  border-radius: 8px;
  padding: 16px;
  backdrop-filter: blur(4px);
}

.table-spacer {
  width: 32px;
}
</style>
