package com.github.aadvorak.artilleryonline.entity.view;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Getter
@Setter
@Accessors(chain = true)
public class UserBattleHistoryView {

    private long battleHistoryId;

    private LocalDateTime beginTime;

    private Short battleTypeId;

    private String vehicleName;

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

    private boolean survived;
}
