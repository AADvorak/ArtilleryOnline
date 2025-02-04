package com.github.aadvorak.artilleryonline.battle.model;

import lombok.Getter;

public class TimeoutUpdate {

    @Getter
    private boolean updated = false;

    private long lastUpdateTime = System.currentTimeMillis();

    public void setUpdatedByTimeout() {
        var currentTime = System.currentTimeMillis();
        if (currentTime - lastUpdateTime > 1000) {
            lastUpdateTime = currentTime;
            updated = true;
        }
    }

    public void setUpdated() {
        lastUpdateTime = System.currentTimeMillis();
        updated = true;
    }

    public void resetUpdated() {
        updated = false;
    }
}
