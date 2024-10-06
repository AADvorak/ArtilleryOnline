package com.github.aadvorak.artilleryonline.battle.processor;

import com.github.aadvorak.artilleryonline.battle.Battle;
import com.github.aadvorak.artilleryonline.battle.BattleStage;
import com.github.aadvorak.artilleryonline.battle.command.Command;

public class BattleStepProcessorBase implements BattleStepProcessor {

    public final void processStep(Battle battle) {
        setDebugFlags(battle);
        if (!battle.isPaused() || battle.isDoStep()) {
            battle.increaseTime();
            if (changeStageIfNeeded(battle)) {
                battle.getModel().setUpdated(true);
            } else {
                doStepLogic(battle);
            }
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
            }
            return true;
        }
        return false;
    }

    private void setDebugFlags(Battle battle) {
        battle.setDoStep(false);
        battle.setForceSend(false);
        var debugCommand = battle.getDebugCommands().poll();
        if (debugCommand != null) {
            if (Command.PAUSE.equals(debugCommand.getCommand())) {
                battle.setPaused(true);
                battle.getModel().setUpdated(true);
            }
            if (Command.RESUME.equals(debugCommand.getCommand())) {
                battle.setPaused(false);
                battle.getModel().setUpdated(true);
            }
            if (Command.STEP.equals(debugCommand.getCommand())) {
                battle.setDoStep(true);
                battle.setForceSend(true);
            }
        }
    }
}
