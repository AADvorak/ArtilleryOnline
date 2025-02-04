package com.github.aadvorak.artilleryonline.battle.model;

import lombok.Getter;

public class TimeoutUpdate {

    @Getter
    private boolean updated = false;

    private long lastUpdateTime = System.currentTimeMillis();

    public boolean setUpdatedByTimeout(long currentTime) {
        if (currentTime - lastUpdateTime > 1000) {
            setUpdated(currentTime);
        }
        return updated;
    }

    public void setUpdated(long currentTime) {
        lastUpdateTime = currentTime;
        updated = true;
    }

    public void resetUpdated() {
        updated = false;
    }
}
