<template>
  <div v-show="showDetails || isTeamBattle" :class="'players-info-container ' + containerClass">
    <div class="teams-container">
      <team-players-info
          :isTeamBattle="isTeamBattle"
          :team-id="0"
          :users-team-id="usersTeamId"
          :team-players="team1Players"
          :show-totals="isTeamBattle && !showDetails"
          :show-details="showDetails"
      />
      <template v-if="isTeamBattle">
        <div class="table-spacer"></div>
        <team-players-info
            is-team-battle
            :team-id="1"
            :users-team-id="usersTeamId"
            :team-players="team2Players"
            :show-totals="!showDetails"
            :show-details="showDetails"
        />
      </template>
    </div>
  </div>
  <div :class="'details-switcher-container ' + containerClass">
    <icon-btn
        :icon="showDetails ? mdiChevronUp : mdiChevronDown"
        :tooltip="t(showDetails ? 'battleHeader.hidePlayersDetails' : 'battleHeader.showPlayersDetails')"
        @click="switchShowDetails"
    />
  </div>
</template>

<script setup lang="ts">
import type {PlayerInfo} from "~/playground/data/common";
import TeamPlayersInfo from "~/playground/components/TeamPlayersInfo.vue";
import {computed} from "vue";
import {useBattleStore} from "~/stores/battle";
import {BattleType} from "~/playground/data/battle";
import {useUserStore} from "~/stores/user";
import {DefaultColors} from "~/dictionary/default-colors";
import {mdiChevronUp, mdiChevronDown} from "@mdi/js";
import {useI18n} from "vue-i18n";
import {useUserSettingsStore} from "~/stores/user-settings";

const props = defineProps<{
  separateHeaderToolbars: boolean
}>()

const {t} = useI18n()

const battleStore = useBattleStore()

const userStore = useUserStore()

const userSettingsStore = useUserSettingsStore()

const usersTeamId = computed<number>(() => {
  return battleStore.battle?.nicknameTeamMap[userStore.user!.nickname] || 0
})

const switchShowDetailsKey = computed(() => {
  return userSettingsStore.controlsOrDefaults
      .filter(item => item.name === 'switchPlayersDetails')[0]?.value
})

const showDetails = ref(false)

const isTeamBattle = computed(() => battleStore.battle?.type === BattleType.TEAM_ELIMINATION)

const team1Players = computed<PlayerInfo[]>(() => {
  return getTeamNicknames(0).map(nicknameToPlayerInfo)
})

const team2Players = computed<PlayerInfo[]>(() => {
  return getTeamNicknames(1).map(nicknameToPlayerInfo)
})

const containerClass = computed(() => {
  return props.separateHeaderToolbars ? 'double-top' : 'small-top'
})

onMounted(() => {
  addEventListener('keyup', switchShowDetailsIfTabPressed)
})

onUnmounted(() => {
  removeEventListener('keyup', switchShowDetailsIfTabPressed)
})

function switchShowDetailsIfTabPressed(e) {
  if (e.code === switchShowDetailsKey.value) {
    e.preventDefault()
    switchShowDetails()
  }
}

function switchShowDetails() {
  showDetails.value = !showDetails.value
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
  const statistics = battleStore.battle?.model.statistics[nickname]
  let color = DefaultColors.VEHICLE
  let hp = 0
  let maxHp = 1
  let damage = 0
  let frags = 0
  if (vehicle) {
    hp = vehicle.state.hitPoints
    maxHp = vehicle.specs.hitPoints
    if (vehicle.config.color) color = vehicle.config.color
  }
  if (statistics) {
    damage = Math.floor(statistics.causedDamage)
    frags = statistics.destroyedVehicles
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
.details-switcher-container {
  position: absolute;
  left: 50%;
  transform: translate(-50%,-30%);
  z-index: 1000;
}

.players-info-container {
  position: absolute;
  left: 50%;
  transform: translateX(-50%);
  z-index: 1000;
  opacity: 0.9;
  pointer-events: none;
}

.small-top {
  top: 36px;
}

.double-top {
  top: 72px;
}

.teams-container {
  display: flex;
  gap: 8px;
  padding: 8px;
}

.table-spacer {
  width: 8px;
}
</style>
