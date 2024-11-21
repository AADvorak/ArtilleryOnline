package com.github.aadvorak.artilleryonline.battle.statistics;

import lombok.Getter;

@Getter
public class UserBattleStatistics {

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

    public void increaseCausedDamage(double causedDamage) {
        this.causedDamage += causedDamage;
    }

    public void increaseMadeShots() {
        madeShots++;
    }

    public void increaseCausedDirectHits() {
        causedDirectHits++;
    }

    public void increaseCausedIndirectHits() {
        causedIndirectHits++;
    }

    public void increaseCausedTrackBreaks() {
        causedTrackBreaks++;
    }

    public void increaseDestroyedVehicles() {
        destroyedVehicles++;
    }

    public void increaseReceivedDamage(double receivedDamage) {
        this.receivedDamage += receivedDamage;
    }

    public void increaseReceivedDirectHits() {
        receivedDirectHits++;
    }

    public void increaseReceivedIndirectHits() {
        receivedIndirectHits++;
    }

    public void increaseReceivedTrackBreaks() {
        receivedTrackBreaks++;
    }
}
