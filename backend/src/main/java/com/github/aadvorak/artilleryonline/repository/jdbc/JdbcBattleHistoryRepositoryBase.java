package com.github.aadvorak.artilleryonline.repository.jdbc;

import com.github.aadvorak.artilleryonline.repository.filters.UserBattleHistoryFilters;

import java.util.HashMap;
import java.util.Map;

public class JdbcBattleHistoryRepositoryBase {

    protected static final String QUERY_BATTLE_TYPE_ID_CONDITION = """
                and bh.battle_type_id = :battleTypeId
            """;
    protected static final String QUERY_VEHICLE_NAME_CONDITION = """
                and ubh.vehicle_name = :vehicleName
            """;
    protected static final String QUERY_DT_FROM_CONDITION = """
                and bh.begin_time >= :dtFrom
            """;
    protected static final String QUERY_DT_TO_CONDITION = """
                and bh.begin_time <= :dtTo
            """;

    protected static final Map<String, String> CONDITIONS_MAP = Map.of(
            "battleTypeId", QUERY_BATTLE_TYPE_ID_CONDITION,
            "vehicleName", QUERY_VEHICLE_NAME_CONDITION,
            "dtFrom", QUERY_DT_FROM_CONDITION,
            "dtTo", QUERY_DT_TO_CONDITION
    );

    protected Map<String, Object> makeParams(long userId, UserBattleHistoryFilters filters) {
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("battleTypeId", filters.getBattleTypeId());
        params.put("vehicleName", filters.getVehicleName());
        params.put("dtFrom", filters.getDtFrom());
        params.put("dtTo", filters.getDtTo());
        return params;
    }
}
