package com.github.aadvorak.artilleryonline.battle.processor;

import com.github.aadvorak.artilleryonline.battle.Battle;
import com.github.aadvorak.artilleryonline.battle.BattleStage;

public class BattleStepProcessorBase implements BattleStepProcessor {

    public final void processStep(Battle battle) {
        increaseTime(battle);
        doStepLogic(battle);
        if (changeStageIfNeeded(battle)) {
            battle.getModel().setUpdated(true);
        }
    }

    private void increaseTime(Battle battle) {
        var previousTime = battle.getTime();
        var currentTime = System.currentTimeMillis() - battle.getBeginTime();
        var currentTimeStep = currentTime - previousTime;
        battle.setTime(currentTime);
        battle.getModel().setCurrentTimeStepSecs((double) currentTimeStep / 1000);
    }

    protected void doStepLogic(Battle battle) {

    }

    protected boolean changeStageIfNeeded(Battle battle) {
        var battleStage = battle.getBattleStage();
        if (battle.getTime() >= battleStage.getMaxTime()) {
            if (BattleStage.WAITING.equals(battleStage)) {
                battle.setStageAndResetTime(BattleStage.ACTIVE);
            } else if (BattleStage.ACTIVE.equals(battleStage)) {
                battle.setStageAndResetTime(BattleStage.FINISHED);
            } else if (BattleStage.FINISHED.equals(battleStage)) {
                battle.setStageAndResetTime(BattleStage.TERMINATE);
            }
            return true;
        }
        return false;
    }
}
