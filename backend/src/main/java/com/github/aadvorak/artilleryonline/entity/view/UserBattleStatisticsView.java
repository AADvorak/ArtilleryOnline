package com.github.aadvorak.artilleryonline.entity.view;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class UserBattleStatisticsView {

    private int battlesPlayed;

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

    private int battlesSurvived;
}
