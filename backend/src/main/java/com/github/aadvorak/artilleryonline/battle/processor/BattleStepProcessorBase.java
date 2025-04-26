package com.github.aadvorak.artilleryonline.battle.processor;

import com.github.aadvorak.artilleryonline.battle.Battle;
import com.github.aadvorak.artilleryonline.battle.BattleStage;
import com.github.aadvorak.artilleryonline.battle.command.Command;
import com.github.aadvorak.artilleryonline.battle.processor.command.ColliderCommandProcessor;
import com.github.aadvorak.artilleryonline.battle.tracking.BattleTracker;

public class BattleStepProcessorBase implements BattleStepProcessor {

    private BattleTracker battleTracker;

    public final void processStep(Battle battle) {
        processDebugCommand(battle);
        if (!battle.getDebug().isPaused() || battle.getDebug().isDoStep()) {
            trackBattle(battle);
            battle.increaseTime();
            if (!changeStageIfNeeded(battle)) {
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

    private void processDebugCommand(Battle battle) {
        battle.getDebug().setDoStep(false);
        battle.getDebug().setForceSend(false);
        var debugCommand = battle.getQueues().getDebugCommands().poll();
        if (debugCommand != null) {
            if (Command.PAUSE.equals(debugCommand.getCommand())) {
                battle.getDebug().setPaused(true);
                battle.getModel().setUpdated(true);
            }
            if (Command.RESUME.equals(debugCommand.getCommand())) {
                battle.getDebug().setPaused(false);
                battle.getModel().setUpdated(true);
            }
            if (Command.STEP.equals(debugCommand.getCommand())) {
                battle.getDebug().setDoStep(true);
                battle.getDebug().setForceSend(true);
            }
            if (Command.START_TRACKING.equals(debugCommand.getCommand())) {
                startTrackingBattle(battle);
            }
            if (Command.STOP_TRACKING.equals(debugCommand.getCommand())) {
                stopTrackingBattle(battle);
            }
            ColliderCommandProcessor.process(debugCommand, battle.getModel());
        }
    }

    private void startTrackingBattle(Battle battle) {
        battleTracker = new BattleTracker(battle);
    }

    private void trackBattle(Battle battle) {
        if (battleTracker != null) {
            battleTracker.appendToCsv(battle);
        }
    }

    private void stopTrackingBattle(Battle battle) {
        if (battleTracker != null) {
            battle.getDebug().setTracking(battleTracker.getCsv());
            battleTracker = null;
        }
    }
}
