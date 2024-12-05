package com.github.aadvorak.artilleryonline.dto.response;

import com.github.aadvorak.artilleryonline.entity.view.UserBattleStatisticsView;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.modelmapper.ModelMapper;

@Getter
@Setter
@Accessors(chain = true)
public class UserBattleStatisticsResponse {

    private int battlesPlayed;

    private double causedDamage;

    private int madeShots;

    private int causedDirectHits;

    private int causedIndirectHits;

    private int causedTrackBreaks;

    private int destroyedVehicles;

    private double receivedDamage;

    private int receivedDirectHits;

    private int receivedIndirectHits;

    private int receivedTrackBreaks;

    private int battlesSurvived;

    public static UserBattleStatisticsResponse of(UserBattleStatisticsView view) {
        return new ModelMapper().map(view, UserBattleStatisticsResponse.class);
    }
}
