package com.github.aadvorak.artilleryonline.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Getter
@Setter
@Accessors(chain = true)
@Table(name = "user_battle_history")
public class UserBattleHistory {

    @EmbeddedId
    private UserBattleHistoryId id;

    @Column
    private String vehicleName;

    @Column
    private double causedDamage;

    @Column
    private int madeShots;

    @Column
    private int causedDirectHits;

    @Column
    private int causedIndirectHits;

    @Column
    private int causedTrackBreaks;

    @Column
    private int destroyedVehicles;

    @Column
    private int destroyedDrones;

    @Column
    private int destroyedMissiles;

    @Column
    private double receivedDamage;

    @Column
    private int receivedDirectHits;

    @Column
    private int receivedIndirectHits;

    @Column
    private int receivedTrackBreaks;

    @Column
    private boolean survived;

    @Column
    private Boolean won;
}
