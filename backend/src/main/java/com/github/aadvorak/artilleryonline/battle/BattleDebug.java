package com.github.aadvorak.artilleryonline.battle;

import com.github.aadvorak.artilleryonline.battle.tracking.BattleTracker;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class BattleDebug {

    private boolean paused = false;

    private boolean doStep = false;

    private boolean forceSend = false;

    private BattleTracker tracker;

    private int colliderObjectId = 1;
}
