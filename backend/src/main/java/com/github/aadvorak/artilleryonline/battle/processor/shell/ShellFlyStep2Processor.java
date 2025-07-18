package com.github.aadvorak.artilleryonline.battle.processor.shell;

import com.github.aadvorak.artilleryonline.battle.calculations.BattleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.ShellCalculations;
import com.github.aadvorak.artilleryonline.battle.model.BattleModel;
import com.github.aadvorak.artilleryonline.battle.processor.Step2Processor;
import com.github.aadvorak.artilleryonline.battle.utils.BattleUtils;
import org.springframework.stereotype.Component;

@Component
public class ShellFlyStep2Processor implements Step2Processor {

    @Override
    public void process(BattleCalculations battle) {
        battle.getShells().forEach(shell -> processShell(shell, battle.getModel()));
    }

    private void processShell(ShellCalculations shell, BattleModel battleModel) {
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
