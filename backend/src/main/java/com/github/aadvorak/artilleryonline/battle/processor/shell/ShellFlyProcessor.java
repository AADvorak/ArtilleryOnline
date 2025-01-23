package com.github.aadvorak.artilleryonline.battle.processor.shell;

import com.github.aadvorak.artilleryonline.battle.calculations.ShellCalculations;
import com.github.aadvorak.artilleryonline.battle.common.Position;
import com.github.aadvorak.artilleryonline.battle.model.BattleModel;
import com.github.aadvorak.artilleryonline.battle.specs.RoomSpecs;

public class ShellFlyProcessor {

    public static void processStep1(ShellCalculations shell, BattleModel battleModel) {
        var position = shell.getPosition();
        var velocity = shell.getVelocity();
        shell.getNext().setPosition(new Position()
                .setX(position.getX() + velocity.getX() * battleModel.getCurrentTimeStepSecs())
                .setY(position.getY() + velocity.getY() * battleModel.getCurrentTimeStepSecs()));
    }

    public static void processStep2(ShellCalculations shell, BattleModel battleModel) {
        var velocity = shell.getVelocity();
        if (positionIsOutOfRoom(shell.getNext().getPosition(), battleModel.getRoom().getSpecs())) {
            velocity.setX(-velocity.getX());
        }
        shell.getModel().getState().setPosition(shell.getNext().getPosition());
        var gravityAcceleration = battleModel.getRoom().getSpecs().getGravityAcceleration();
        velocity.setY(velocity.getY() - gravityAcceleration * battleModel.getCurrentTimeStepSecs());
    }

    private static boolean positionIsOutOfRoom(Position position, RoomSpecs roomSpecs) {
        var xMax = roomSpecs.getRightTop().getX();
        var xMin = roomSpecs.getLeftBottom().getX();
        return position.getX() >= xMax || position.getX() <= xMin;
    }
}
