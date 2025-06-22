package com.github.aadvorak.artilleryonline.battle.processor.command;

import com.github.aadvorak.artilleryonline.battle.command.Command;
import com.github.aadvorak.artilleryonline.battle.command.DebugCommand;
import com.github.aadvorak.artilleryonline.battle.model.BattleModel;

public class ColliderCommandProcessor {

    public static void process(DebugCommand command, BattleModel battleModel) {
        if (Command.STOP_ALL.equals(command.getCommand())) {
            stopAll(battleModel);
            return;
        }

        var vehicle = battleModel.getVehicles().values().iterator().next();

        if (vehicle == null) {
            return;
        }

        if (Command.START_PUSHING.equals(command.getCommand())) {
            var direction = command.getParams().getDirection();
            vehicle.getState().setPushingDirection(direction);
            vehicle.getUpdate().setUpdated();
        }

        if (Command.STOP_PUSHING.equals(command.getCommand())) {
            var direction = command.getParams().getDirection();
            var vehicleDirection = vehicle.getState().getPushingDirection();
            if (direction != null && direction.equals(vehicleDirection)) {
                vehicle.getState().setPushingDirection(null);
                vehicle.getUpdate().setUpdated();
            }
        }

        if (Command.START_ROTATING.equals(command.getCommand())) {
            var direction = command.getParams().getDirection();
            vehicle.getState().setRotatingDirection(direction);
            vehicle.getUpdate().setUpdated();
        }

        if (Command.STOP_ROTATING.equals(command.getCommand())) {
            var direction = command.getParams().getDirection();
            var vehicleDirection = vehicle.getState().getRotatingDirection();
            if (direction != null && direction.equals(vehicleDirection)) {
                vehicle.getState().setRotatingDirection(null);
                vehicle.getUpdate().setUpdated();
            }
        }
    }

    private static void stopAll(BattleModel battleModel) {
        battleModel.getVehicles().values().forEach(vehicle -> {
            vehicle.getState().getVelocity()
                    .setX(0)
                    .setY(0)
                    .setAngle(0);
        });
    }
}
