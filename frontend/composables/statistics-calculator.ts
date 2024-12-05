import type {UserBattleStatistics, UserBattleStatisticsPerBattle} from "~/data/model";

export function useStatisticsCalculator() {

  function calculatePerBattle(statistics: UserBattleStatistics): UserBattleStatisticsPerBattle {
    return {
      causedDamage: statistics.causedDamage / statistics.battlesPlayed,
      madeShots: statistics.madeShots / statistics.battlesPlayed,
      causedDirectHits: statistics.causedDirectHits / statistics.battlesPlayed,
      causedIndirectHits: statistics.causedIndirectHits / statistics.battlesPlayed,
      causedTrackBreaks: statistics.causedTrackBreaks / statistics.battlesPlayed,
      destroyedVehicles: statistics.destroyedVehicles / statistics.battlesPlayed,
      receivedDamage: statistics.receivedDamage / statistics.battlesPlayed,
      receivedDirectHits: statistics.receivedDirectHits / statistics.battlesPlayed,
      receivedIndirectHits: statistics.receivedIndirectHits / statistics.battlesPlayed,
      receivedTrackBreaks: statistics.receivedTrackBreaks / statistics.battlesPlayed
    }
  }

  return {calculatePerBattle}
}
