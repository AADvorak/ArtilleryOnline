package com.github.aadvorak.artilleryonline.battle.processor;

import com.github.aadvorak.artilleryonline.battle.Battle;
import com.github.aadvorak.artilleryonline.battle.BattleStage;

public class BattleStepProcessorBase implements BattleStepProcessor {

    public final void processStep(Battle battle) {
        battle.increaseTime();
        if (changeStageIfNeeded(battle)) {
            battle.getModel().setUpdated(true);
        } else {
            doStepLogic(battle);
        }
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
