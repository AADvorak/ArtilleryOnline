package com.github.aadvorak.artilleryonline.repository.filters;

import com.github.aadvorak.artilleryonline.battle.BattleType;
import com.github.aadvorak.artilleryonline.dto.request.UserBattleHistoryFiltersRequest;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Optional;

@Getter
@Setter
@Accessors(chain = true)
public class UserBattleHistoryFilters {

    private Short battleTypeId;

    private String vehicleName;

    public static UserBattleHistoryFilters of(UserBattleHistoryFiltersRequest request) {
        var filters = new UserBattleHistoryFilters();
        if (request != null) {
            Optional.ofNullable(request.getBattleType())
                    .map(BattleType::getId)
                    .ifPresent(filters::setBattleTypeId);
            filters.setVehicleName(request.getVehicleName());
        }
        return filters;
    }
}
