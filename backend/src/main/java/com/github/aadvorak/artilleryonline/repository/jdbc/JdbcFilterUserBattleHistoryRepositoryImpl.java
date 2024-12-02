package com.github.aadvorak.artilleryonline.repository.jdbc;

import com.github.aadvorak.artilleryonline.entity.view.UserBattleHistoryView;
import com.github.aadvorak.artilleryonline.repository.FilterUserBattleHistoryRepository;
import com.github.aadvorak.artilleryonline.repository.filters.UserBattleHistoryFilters;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class JdbcFilterUserBattleHistoryRepositoryImpl implements FilterUserBattleHistoryRepository {

    private static final String QUERY_BASE = """
            select {}
            from battle_history bh
            join public.user_battle_history ubh on bh.id = ubh.battle_history_id
            where ubh.user_id = :userId
            """;
    private static final String QUERY_BATTLE_TYPE_ID_CONDITION = """
                and bh.battle_type_id = :battleTypeId
            """;
    private static final String QUERY_VEHICLE_NAME_CONDITION = """
                and ubh.vehicle_name = :vehicleName
            """;
    private static final String QUERY_DT_FROM_CONDITION = """
                and bh.begin_time >= :dtFrom
            """;
    private static final String QUERY_DT_TO_CONDITION = """
                and bh.begin_time <= :dtTo
            """;

    private static final RowMapper<UserBattleHistoryView> ROW_MAPPER = (rs, rowNum) ->
            new UserBattleHistoryView()
                    .setBattleHistoryId(rs.getLong("battle_history_id"))
                    .setBeginTime(rs.getTimestamp("begin_time").toLocalDateTime())
                    .setBattleTypeId(rs.getShort("battle_type_id"))
                    .setVehicleName(rs.getString("vehicle_name"))
                    .setCausedDamage(rs.getFloat("caused_damage"))
                    .setMadeShots(rs.getInt("made_shots"))
                    .setCausedDirectHits(rs.getInt("caused_direct_hits"))
                    .setCausedIndirectHits(rs.getInt("caused_indirect_hits"))
                    .setCausedTrackBreaks(rs.getInt("caused_track_breaks"))
                    .setDestroyedVehicles(rs.getInt("destroyed_vehicles"))
                    .setReceivedDamage(rs.getFloat("received_damage"))
                    .setReceivedDirectHits(rs.getInt("received_direct_hits"))
                    .setReceivedIndirectHits(rs.getInt("received_indirect_hits"))
                    .setReceivedTrackBreaks(rs.getInt("received_track_breaks"))
                    .setSurvived(rs.getBoolean("survived"));

    private final JdbcPageQueryExecutor<UserBattleHistoryView> queryExecutor;

    public JdbcFilterUserBattleHistoryRepositoryImpl(NamedParameterJdbcTemplate jdbcTemplate) {
        queryExecutor = new JdbcPageQueryExecutor<>(QUERY_BASE, Map.of(
                "battleTypeId", QUERY_BATTLE_TYPE_ID_CONDITION,
                "vehicleName", QUERY_VEHICLE_NAME_CONDITION,
                "dtFrom", QUERY_DT_FROM_CONDITION,
                "dtTo", QUERY_DT_TO_CONDITION
        ), ROW_MAPPER, jdbcTemplate);
    }
    @Override
    public Page<UserBattleHistoryView> findByUserIdAndFilters(
            long userId, UserBattleHistoryFilters filters, Pageable pageable
    ) {
        Map<String, Object> params = makeParams(userId, filters);
        return queryExecutor.execute(params, pageable);
    }

    private Map<String, Object> makeParams(long userId, UserBattleHistoryFilters filters) {
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("battleTypeId", filters.getBattleTypeId());
        params.put("vehicleName", filters.getVehicleName());
        params.put("dtFrom", filters.getDtFrom());
        params.put("dtTo", filters.getDtTo());
        return params;
    }
}
