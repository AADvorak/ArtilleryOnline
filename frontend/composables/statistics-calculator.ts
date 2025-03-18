import type {UserBattleStatistics, UserBattleStatisticsCoefficients, UserBattleStatisticsPerBattle} from "~/data/model";

export function useStatisticsCalculator() {

  function calculatePerBattle(statistics: UserBattleStatistics): UserBattleStatisticsPerBattle | undefined {
    if (statistics.battlesPlayed === 0) {
      return undefined
    }
    return {
      causedDamage: statistics.causedDamage / statistics.battlesPlayed,
      madeShots: statistics.madeShots / statistics.battlesPlayed,
      causedDirectHits: statistics.causedDirectHits / statistics.battlesPlayed,
      causedIndirectHits: statistics.causedIndirectHits / statistics.battlesPlayed,
      causedTrackBreaks: statistics.causedTrackBreaks / statistics.battlesPlayed,
      destroyedVehicles: statistics.destroyedVehicles / statistics.battlesPlayed,
      destroyedDrones: statistics.destroyedDrones / statistics.battlesPlayed,
      destroyedMissiles: statistics.destroyedMissiles / statistics.battlesPlayed,
      receivedDamage: statistics.receivedDamage / statistics.battlesPlayed,
      receivedDirectHits: statistics.receivedDirectHits / statistics.battlesPlayed,
      receivedIndirectHits: statistics.receivedIndirectHits / statistics.battlesPlayed,
      receivedTrackBreaks: statistics.receivedTrackBreaks / statistics.battlesPlayed
    }
  }

  function calculateCoefficients(statistics: UserBattleStatistics): UserBattleStatisticsCoefficients {
    return {
      survivalRate: statistics.battlesPlayed ? Math.round(100 * statistics.battlesSurvived / statistics.battlesPlayed) : 0,
      directHitRate: statistics.madeShots ? Math.round(100 * statistics.causedDirectHits / statistics.madeShots) : 0,
      indirectHitRate: statistics.madeShots ? Math.round(100 * statistics.causedIndirectHits / statistics.madeShots) : 0,
      trackBreakRate: statistics.madeShots ? Math.round(100 * statistics.causedTrackBreaks / statistics.madeShots) : 0,
      damagePerShot: statistics.madeShots ? statistics.causedDamage / statistics.madeShots : 0
    }
  }

  return {calculatePerBattle, calculateCoefficients}
}
