package com.github.aadvorak.artilleryonline.battle.processor.shell;

import com.github.aadvorak.artilleryonline.battle.Battle;
import com.github.aadvorak.artilleryonline.battle.common.Position;
import com.github.aadvorak.artilleryonline.battle.model.BattleModel;
import com.github.aadvorak.artilleryonline.battle.model.ShellModel;
import com.github.aadvorak.artilleryonline.battle.model.VehicleModel;
import com.github.aadvorak.artilleryonline.battle.specs.RoomSpecs;

public class ShellFlyProcessor {

    public static void processStep(ShellModel shellModel, BattleModel battleModel) {
        var prevPosition = shellModel.getState().getPosition();
        var velocity = shellModel.getState().getVelocity();
        var angle = shellModel.getState().getAngle();
        var velocityX = velocity * Math.cos(angle);
        var velocityY = velocity * Math.sin(angle);
        var timeStep = Battle.getTimeStepSecs();
        var nextPosition = new Position()
                .setX(prevPosition.getX() + velocityX * timeStep)
                .setY(prevPosition.getY() + velocityY * timeStep);
        if (positionIsOutOfRoom(nextPosition, battleModel.getRoom().getSpecs())) {
            battleModel.removeShellById(shellModel.getId());
            return;
        }
        var hitVehicle = getHitVehicle(nextPosition, battleModel);
        if (hitVehicle != null) {
            ShellDamageProcessor.process(hitVehicle, shellModel.getSpecs(), battleModel);
            battleModel.removeShellById(shellModel.getId());
            return;
        }
        // todo hit ground
        var gravityAcceleration = battleModel.getRoom().getSpecs().getGravityAcceleration();
        velocityY = velocityY - gravityAcceleration * Battle.getTimeStepSecs();
        velocity = Math.sqrt(Math.pow(velocityX, 2.0) + Math.pow(velocityY, 2.0));
        angle = Math.atan(velocityY / velocityX) + (velocityX < 0 ? Math.PI : 0.0);
        shellModel.getState().setPosition(nextPosition);
        shellModel.getState().setVelocity(velocity);
        shellModel.getState().setAngle(angle);
    }

    private static VehicleModel getHitVehicle(Position nextPosition, BattleModel battleModel) {
        for (var vehicleModel : battleModel.getVehicles().values()) {
            if (positionIsInVehicle(nextPosition, vehicleModel)) {
                return vehicleModel;
            }
        }
        return null;
    }

    private static boolean positionIsInVehicle(Position position, VehicleModel vehicleModel) {
        var vehiclePosition = vehicleModel.getState().getPosition();
        var vehicleRadius = vehicleModel.getSpecs().getRadius();
        // todo logic with vehicle angle
        var distanceToVehicleCenter = position.distanceTo(vehiclePosition);
        return position.getY() > vehiclePosition.getY() && distanceToVehicleCenter <= vehicleRadius;
    }

    private static boolean positionIsOutOfRoom(Position position, RoomSpecs roomSpecs) {
        var xMax = roomSpecs.getRightTop().getX();
        var xMin = roomSpecs.getLeftBottom().getX();
        var yMin = roomSpecs.getLeftBottom().getY();
        return position.getX() >= xMax || position.getX() <= xMin || position.getY() <= yMin;
    }
}
