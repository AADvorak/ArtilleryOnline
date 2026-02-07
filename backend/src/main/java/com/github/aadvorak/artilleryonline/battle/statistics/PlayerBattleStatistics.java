package com.github.aadvorak.artilleryonline.battle.statistics;

import lombok.Getter;

@Getter
public class PlayerBattleStatistics {

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

    private boolean updated = false;

    public void increaseCausedDamage(double causedDamage) {
        this.causedDamage += causedDamage;
        this.updated = true;
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
        this.updated = true;
    }

    public void increaseDestroyedDrones() {
        destroyedDrones++;
    }

    public void increaseDestroyedMissiles() {
        destroyedMissiles++;
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

    public void resetUpdated() {
        this.updated = false;
    }
}
