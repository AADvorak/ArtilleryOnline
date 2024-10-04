package com.github.aadvorak.artilleryonline.battle.processor;

import com.github.aadvorak.artilleryonline.battle.Battle;
import com.github.aadvorak.artilleryonline.battle.BattleStage;
import com.github.aadvorak.artilleryonline.battle.command.Command;

public class BattleStepProcessorBase implements BattleStepProcessor {

    public final void processStep(Battle battle) {
        if (checkDoStep(battle)) {
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

    private boolean checkDoStep(Battle battle) {
        var debugCommand = battle.getDebugCommands().poll();
        var doStep = false;
        if (debugCommand != null) {
            if (Command.PAUSE.equals(debugCommand.getCommand())) {
                battle.setPaused(true);
            }
            if (Command.RESUME.equals(debugCommand.getCommand())) {
                battle.setPaused(false);
            }
            if (Command.STEP.equals(debugCommand.getCommand())) {
                doStep = true;
            }
        }
        return !battle.isPaused() || doStep;
    }
}
