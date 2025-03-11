package com.github.aadvorak.artilleryonline.battle.processor.shell;

import com.github.aadvorak.artilleryonline.battle.calculations.ShellCalculations;
import com.github.aadvorak.artilleryonline.battle.model.BattleModel;
import com.github.aadvorak.artilleryonline.battle.utils.BattleUtils;

public class ShellFlyProcessor {

    public static void processStep1(ShellCalculations shell, BattleModel battleModel) {
        if (shell.getModel().getState().isStuck()) {
            shell.getModel().getState().setStuckTime(shell.getModel().getState().getStuckTime()
                    + battleModel.getCurrentTimeStepSecs());
            if (shell.getModel().getState().getStuckTime() > 3.0) {
                BombDropProcessor.drop(shell.getPosition(), shell.getModel().getVehicleId(), battleModel);
                battleModel.getUpdates().removeShell(shell.getId());
            }
            return;
        }
        shell.calculateNextPosition(battleModel.getCurrentTimeStepSecs());
    }

    public static void processStep2(ShellCalculations shell, BattleModel battleModel) {
        if (shell.getModel().getState().isStuck()) {
            return;
        }
        if (!shell.getCollisions().isEmpty()) {
            return;
        }
        var velocity = shell.getVelocity();
        if (BattleUtils.positionIsOutOfRoom(shell.getNext().getPosition(), battleModel.getRoom().getSpecs())) {
            velocity.setX(-velocity.getX());
        }
        shell.getModel().getState().setPosition(shell.getNext().getPosition());
        var gravityAcceleration = battleModel.getRoom().getSpecs().getGravityAcceleration();
        velocity.setY(velocity.getY() - gravityAcceleration * battleModel.getCurrentTimeStepSecs());
    }
}
