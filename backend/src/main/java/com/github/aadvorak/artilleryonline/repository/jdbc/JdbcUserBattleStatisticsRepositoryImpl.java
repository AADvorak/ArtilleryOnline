package com.github.aadvorak.artilleryonline.repository.jdbc;

import com.github.aadvorak.artilleryonline.entity.view.UserBattleStatisticsView;
import com.github.aadvorak.artilleryonline.repository.UserBattleStatisticsRepository;
import com.github.aadvorak.artilleryonline.repository.filters.UserBattleHistoryFilters;
import com.github.aadvorak.artilleryonline.repository.utils.SqlUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JdbcUserBattleStatisticsRepositoryImpl extends JdbcBattleHistoryRepositoryBase
        implements UserBattleStatisticsRepository {

    private static final String QUERY_BASE = """
            select
                count(bh.id) as battles_played,
                sum(case when bh.battle_type_id = 6 then 1 else 0 end) as team_battles_played,
                sum(ubh.caused_damage) as caused_damage,
                sum(ubh.made_shots) as made_shots,
                sum(ubh.caused_direct_hits) as caused_direct_hits,
                sum(ubh.caused_indirect_hits) as caused_indirect_hits,
                sum(ubh.caused_track_breaks) as caused_track_breaks,
                sum(ubh.destroyed_vehicles) as destroyed_vehicles,
                sum(ubh.destroyed_drones) as destroyed_drones,
                sum(ubh.destroyed_missiles) as destroyed_missiles,
                sum(ubh.received_damage) as received_damage,
                sum(ubh.received_direct_hits) as received_direct_hits,
                sum(ubh.received_indirect_hits) as received_indirect_hits,
                sum(ubh.received_track_breaks) as received_track_breaks,
                sum(ubh.survived::int) as battles_survived,
                sum(ubh.won::int) as battles_won
            from battle_history bh
                join public.user_battle_history ubh on bh.id = ubh.battle_history_id
            where ubh.user_id = :userId
            """;

    private static final RowMapper<UserBattleStatisticsView> ROW_MAPPER = (rs, rowNum) ->
            new UserBattleStatisticsView()
                    .setBattlesPlayed(rs.getInt("battles_played"))
                    .setTeamBattlesPlayed(rs.getInt("team_battles_played"))
                    .setCausedDamage(rs.getFloat("caused_damage"))
                    .setMadeShots(rs.getInt("made_shots"))
                    .setCausedDirectHits(rs.getInt("caused_direct_hits"))
                    .setCausedIndirectHits(rs.getInt("caused_indirect_hits"))
                    .setCausedTrackBreaks(rs.getInt("caused_track_breaks"))
                    .setDestroyedVehicles(rs.getInt("destroyed_vehicles"))
                    .setDestroyedDrones(rs.getInt("destroyed_drones"))
                    .setDestroyedMissiles(rs.getInt("destroyed_missiles"))
                    .setReceivedDamage(rs.getFloat("received_damage"))
                    .setReceivedDirectHits(rs.getInt("received_direct_hits"))
                    .setReceivedIndirectHits(rs.getInt("received_indirect_hits"))
                    .setReceivedTrackBreaks(rs.getInt("received_track_breaks"))
                    .setBattlesSurvived(rs.getInt("battles_survived"))
                    .setBattlesWon(rs.getInt("battles_won"));

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public UserBattleStatisticsView findByUserIdAndFilters(long userId, UserBattleHistoryFilters filters) {
        var params = makeParams(userId, filters);
        var query = QUERY_BASE + SqlUtils.makeConditionsQuery(params, CONDITIONS_MAP);
        return jdbcTemplate.queryForObject(query, params, ROW_MAPPER);
    }
}
