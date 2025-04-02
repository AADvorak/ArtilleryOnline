package com.github.aadvorak.artilleryonline.battle.processor.command;

import com.github.aadvorak.artilleryonline.battle.command.Command;
import com.github.aadvorak.artilleryonline.battle.command.UserCommand;
import com.github.aadvorak.artilleryonline.battle.model.BattleModel;
import com.github.aadvorak.artilleryonline.battle.processor.vehicle.VehicleLaunchDroneProcessor;
import com.github.aadvorak.artilleryonline.battle.processor.vehicle.VehicleLaunchMissileProcessor;

public class CommandProcessor {

    public static void process(String userNickname, UserCommand userCommand, BattleModel battleModel) {
        var userVehicle = battleModel.getVehicles().get(userNickname);
        if (userVehicle == null) {
            return;
        }

        if (Command.START_MOVING.equals(userCommand.getCommand())) {
            var direction = userCommand.getParams().getDirection();
            userVehicle.getState().setMovingDirection(direction);
            userVehicle.setUpdated(true);
        }

        if (Command.STOP_MOVING.equals(userCommand.getCommand())) {
            var direction = userCommand.getParams().getDirection();
            var vehicleDirection = userVehicle.getState().getMovingDirection();
            if (direction != null && direction.equals(vehicleDirection)) {
                userVehicle.getState().setMovingDirection(null);
                userVehicle.setUpdated(true);
            }
        }

        if (Command.PUSH_TRIGGER.equals(userCommand.getCommand())) {
            userVehicle.getState().getGunState().setTriggerPushed(true);
        }

        if (Command.RELEASE_TRIGGER.equals(userCommand.getCommand())) {
            userVehicle.getState().getGunState().setTriggerPushed(false);
        }

        if (Command.START_GUN_ROTATING.equals(userCommand.getCommand())) {
            var direction = userCommand.getParams().getDirection();
            userVehicle.getState().getGunState().setRotatingDirection(direction);
            userVehicle.setUpdated(true);
        }

        if (Command.STOP_GUN_ROTATING.equals(userCommand.getCommand())) {
            var direction = userCommand.getParams().getDirection();
            var vehicleDirection = userVehicle.getState().getGunState().getRotatingDirection();
            if (direction != null && direction.equals(vehicleDirection)) {
                userVehicle.getState().getGunState().setRotatingDirection(null);
                userVehicle.setUpdated(true);
            }
        }

        if (Command.SELECT_SHELL.equals(userCommand.getCommand())) {
            var shellName = userCommand.getParams().getShellName();
            var ammo = userVehicle.getState().getAmmo();
            if (ammo.containsKey(shellName) && ammo.get(shellName) > 0) {
                var gunState = userVehicle.getState().getGunState();
                gunState.setSelectedShell(shellName);
                gunState.setLoadedShell(null);
                gunState.setLoadingShell(null);
            }
        }

        if (Command.JET_ON.equals(userCommand.getCommand())) {
            if (userVehicle.getState().getJetState() != null) {
                userVehicle.getState().getJetState().setActive(true);
                userVehicle.setUpdated(true);
            }
        }

        if (Command.JET_OFF.equals(userCommand.getCommand())) {
            if (userVehicle.getState().getJetState() != null) {
                userVehicle.getState().getJetState().setActive(false);
                userVehicle.setUpdated(true);
            }
        }

        if (Command.LAUNCH_MISSILE.equals(userCommand.getCommand())) {
            VehicleLaunchMissileProcessor.launch(userVehicle, battleModel);
        }

        if (Command.LAUNCH_DRONE.equals(userCommand.getCommand())) {
            VehicleLaunchDroneProcessor.launch(userVehicle, battleModel);
        }

        if (Command.SWITCH_GUN_MODE.equals(userCommand.getCommand())) {
            userVehicle.getState().getGunState().setFixed(!userVehicle.getState().getGunState().isFixed());
            userVehicle.setUpdated(true);
        }
    }
}
