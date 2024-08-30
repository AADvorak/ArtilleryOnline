package com.github.aadvorak.artilleryonline.battle.processor;

import com.github.aadvorak.artilleryonline.battle.Battle;

public class BattleStepProcessorBase implements BattleStepProcessor {

    public final void processStep(Battle battle) {
        increaseTime(battle);
        doStepLogic(battle);
        changeStageIfNeeded(battle);
    }

    private void increaseTime(Battle battle) {
        battle.setTime(battle.getTime() + Battle.TIME_STEP_MS);
    }

    protected void doStepLogic(Battle battle) {

    }

    protected void changeStageIfNeeded(Battle battle) {

    }
}
