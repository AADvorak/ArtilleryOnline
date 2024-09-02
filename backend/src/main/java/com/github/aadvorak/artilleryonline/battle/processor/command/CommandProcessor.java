package com.github.aadvorak.artilleryonline.battle.processor.command;

import com.github.aadvorak.artilleryonline.battle.command.Command;
import com.github.aadvorak.artilleryonline.battle.command.UserCommand;
import com.github.aadvorak.artilleryonline.battle.model.BattleModel;

public class CommandProcessor {

    public static void process(String userKey, UserCommand userCommand, BattleModel battleModel) {
        var userVehicle = battleModel.getVehicles().get(userKey);
        if (userVehicle == null) {
            return;
        }

        if (Command.START_MOVING.equals(userCommand.getCommand())) {
            var direction = userCommand.getParams().getDirection();
            userVehicle.getState().setMovingDirection(direction);
        }

        if (Command.STOP_MOVING.equals(userCommand.getCommand())) {
            userVehicle.getState().setMovingDirection(null);
        }

        if (Command.PUSH_TRIGGER.equals(userCommand.getCommand())) {
            userVehicle.getState().getGunState().setTriggerPushed(true);
        }

        if (Command.RELEASE_TRIGGER.equals(userCommand.getCommand())) {
            userVehicle.getState().getGunState().setTriggerPushed(false);
        }

        if (Command.START_GUN_ROTATING.equals(userCommand.getCommand())) {
            var direction = userCommand.getParams().getDirection();
            userVehicle.getState().setGunRotatingDirection(direction);
        }

        if (Command.STOP_GUN_ROTATING.equals(userCommand.getCommand())) {
            userVehicle.getState().setGunRotatingDirection(null);
        }
    }
}
