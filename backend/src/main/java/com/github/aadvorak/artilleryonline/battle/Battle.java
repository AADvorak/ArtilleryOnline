package com.github.aadvorak.artilleryonline.battle;

import com.github.aadvorak.artilleryonline.battle.model.BattleModel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class Battle {

    public static final long TIME_STEP_MS = 100;

    public static double getTimeStepSecs() {
        return (double) TIME_STEP_MS / 1000;
    }

    private BattleModel model;

    private long time;

    private BattleStage battleStage;
}
