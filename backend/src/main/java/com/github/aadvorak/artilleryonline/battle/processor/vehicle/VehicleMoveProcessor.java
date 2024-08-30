package com.github.aadvorak.artilleryonline.battle.processor.vehicle;

import com.github.aadvorak.artilleryonline.battle.Battle;
import com.github.aadvorak.artilleryonline.battle.common.MovingDirection;
import com.github.aadvorak.artilleryonline.battle.model.BattleModel;
import com.github.aadvorak.artilleryonline.battle.model.VehicleModel;

import java.util.stream.Collectors;

public class VehicleMoveProcessor {

    public static void processStep(VehicleModel vehicleModel, BattleModel battleModel) {
        if (vehicleModel.getState().getMovingDirection() == null
                || !canMove(vehicleModel, battleModel)) {
            return;
        }
        doMoveStep(vehicleModel, battleModel);
    }

    private static boolean canMove(VehicleModel vehicleModel, BattleModel battleModel) {
        return noWallCollide(vehicleModel, battleModel) && noVehicleCollide(vehicleModel, battleModel);
    }

    private static boolean noWallCollide(VehicleModel vehicleModel, BattleModel battleModel) {
        var direction = vehicleModel.getState().getMovingDirection();
        var vehicleX = vehicleModel.getState().getPosition().getX();
        var xMax = battleModel.getRoom().getSpecs().getRightTop().getX();
        var xMin = battleModel.getRoom().getSpecs().getLeftBottom().getX();
        return MovingDirection.RIGHT.equals(direction) && vehicleX <= xMax
                || MovingDirection.LEFT.equals(direction) && vehicleX >= xMin;
    }

    private static boolean noVehicleCollide(VehicleModel vehicleModel, BattleModel battleModel) {
        var direction = vehicleModel.getState().getMovingDirection();
        var otherVehicleModels = battleModel.getVehicles().values().stream()
                .filter(value -> value.getId() != vehicleModel.getId())
                .collect(Collectors.toSet());
        var currentVehicleX = vehicleModel.getState().getPosition().getX();
        var currentVehicleRadius = vehicleModel.getSpecs().getRadius();
        var currentVehicleRight = currentVehicleX + currentVehicleRadius;
        var currentVehicleLeft = currentVehicleX - currentVehicleRadius;
        for (var otherVehicleModel : otherVehicleModels) {
            var otherVehicleX = otherVehicleModel.getState().getPosition().getX();
            var otherVehicleRadius = otherVehicleModel.getSpecs().getRadius();
            var otherVehicleRight = otherVehicleX + otherVehicleRadius;
            var otherVehicleLeft = otherVehicleX - otherVehicleRadius;
            if (MovingDirection.RIGHT.equals(direction) && currentVehicleRight >= otherVehicleLeft
                    || MovingDirection.LEFT.equals(direction) && currentVehicleLeft <= otherVehicleRight) {
                return false;
            }
        }
        return true;
    }

    private static void doMoveStep(VehicleModel vehicleModel, BattleModel battleModel) {
        // todo more detailed algorithm
        var position = vehicleModel.getState().getPosition();
        position.setX(position.getX() + getVelocity(vehicleModel) * Battle.getTimeStepSecs());
    }

    private static double getVelocity(VehicleModel vehicleModel) {
        var direction = vehicleModel.getState().getMovingDirection();
        var velocity = vehicleModel.getSpecs().getMovingVelocity();
        if (MovingDirection.RIGHT.equals(direction)) {
            return velocity;
        }
        return -velocity;
    }
}
