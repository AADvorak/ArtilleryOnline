package com.github.aadvorak.artilleryonline.model;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class UserBattleResult {

    private boolean survived;

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
}
