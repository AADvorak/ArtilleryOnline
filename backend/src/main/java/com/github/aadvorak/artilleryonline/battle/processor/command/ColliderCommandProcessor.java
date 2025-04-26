package com.github.aadvorak.artilleryonline.battle.processor.command;

import com.github.aadvorak.artilleryonline.battle.command.Command;
import com.github.aadvorak.artilleryonline.battle.command.DebugCommand;
import com.github.aadvorak.artilleryonline.battle.model.BattleModel;

public class ColliderCommandProcessor {

    public static void process(DebugCommand command, BattleModel battleModel) {
        var vehicle = battleModel.getVehicles().values().iterator().next();

        if (vehicle == null) {
            return;
        }

        if (Command.START_PUSHING.equals(command.getCommand())) {
            var direction = command.getParams().getDirection();
            vehicle.getState().setPushingDirection(direction);
            vehicle.setUpdated(true);
        }

        if (Command.STOP_PUSHING.equals(command.getCommand())) {
            var direction = command.getParams().getDirection();
            var vehicleDirection = vehicle.getState().getPushingDirection();
            if (direction != null && direction.equals(vehicleDirection)) {
                vehicle.getState().setPushingDirection(null);
                vehicle.setUpdated(true);
            }
        }

        if (Command.START_ROTATING.equals(command.getCommand())) {
            var direction = command.getParams().getDirection();
            vehicle.getState().setRotatingDirection(direction);
            vehicle.setUpdated(true);
        }

        if (Command.STOP_ROTATING.equals(command.getCommand())) {
            var direction = command.getParams().getDirection();
            var vehicleDirection = vehicle.getState().getRotatingDirection();
            if (direction != null && direction.equals(vehicleDirection)) {
                vehicle.getState().setRotatingDirection(null);
                vehicle.setUpdated(true);
            }
        }
    }
}
