package com.github.aadvorak.artilleryonline.repository.filters;

import com.github.aadvorak.artilleryonline.battle.BattleType;
import com.github.aadvorak.artilleryonline.dto.request.UserBattleHistoryFiltersRequest;
import com.github.aadvorak.artilleryonline.utils.DateTimeUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.Optional;

@Getter
@Setter
@Accessors(chain = true)
public class UserBattleHistoryFilters {

    private Short battleTypeId;

    private String vehicleName;

    private LocalDateTime dtFrom;

    private LocalDateTime dtTo;

    public static UserBattleHistoryFilters of(UserBattleHistoryFiltersRequest request) {
        var filters = new UserBattleHistoryFilters();
        if (request != null) {
            Optional.ofNullable(request.getBattleType())
                    .map(BattleType::getId)
                    .ifPresent(filters::setBattleTypeId);
            filters.setVehicleName(request.getVehicleName());
            Optional.ofNullable(request.getDtFrom())
                    .map(dt -> DateTimeUtils.parseLocalDateTime(dt, "dtFrom"))
                    .ifPresent(filters::setDtFrom);
            Optional.ofNullable(request.getDtTo())
                    .map(dt -> DateTimeUtils.parseLocalDateTime(dt, "dtTo"))
                    .ifPresent(filters::setDtTo);
        }
        return filters;
    }
}
