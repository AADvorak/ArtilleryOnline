package com.github.aadvorak.artilleryonline.repository.filters;

import com.github.aadvorak.artilleryonline.dto.request.UserBattleHistoryFiltersRequest;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class UserBattleHistoryFilters {

    private Short battleTypeId;

    public static UserBattleHistoryFilters of(UserBattleHistoryFiltersRequest request) {
        var filters = new UserBattleHistoryFilters();
        if (request != null) {
            filters.setBattleTypeId(request.getBattleType().getId());
        }
        return filters;
    }
}
