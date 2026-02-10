package com.github.aadvorak.artilleryonline.dto.response;

import com.github.aadvorak.artilleryonline.battle.BattleType;
import com.github.aadvorak.artilleryonline.entity.view.UserBattleHistoryView;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;
import java.util.Arrays;

@Getter
@Setter
@Accessors(chain = true)
public class UserBattleHistoryResponse {

    private long battleHistoryId;

    private LocalDateTime beginTime;

    private BattleType battleType;

    private String vehicleName;

    private double causedDamage;

    private int madeShots;

    private int causedDirectHits;

    private int causedIndirectHits;

    private int causedTrackBreaks;

    private int destroyedVehicles;

    private int destroyedDrones;

    private int destroyedMissiles;

    private double receivedDamage;

    private int receivedDirectHits;

    private int receivedIndirectHits;

    private int receivedTrackBreaks;

    private boolean survived;

    private Boolean won;

    public static UserBattleHistoryResponse of(UserBattleHistoryView view) {
        return new ModelMapper().map(view, UserBattleHistoryResponse.class)
                .setBattleType(Arrays.stream(BattleType.values())
                        .filter(bt -> bt.getId().equals(view.getBattleTypeId()))
                        .findAny()
                        .orElse(null));
    }
}
