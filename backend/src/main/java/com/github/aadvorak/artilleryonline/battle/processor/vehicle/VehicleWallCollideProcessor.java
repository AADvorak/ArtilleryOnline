package com.github.aadvorak.artilleryonline.battle.processor.vehicle;

import com.github.aadvorak.artilleryonline.battle.calculations.BattleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.Collision;
import com.github.aadvorak.artilleryonline.battle.calculations.VehicleCalculations;
import com.github.aadvorak.artilleryonline.battle.model.VehicleModel;
import com.github.aadvorak.artilleryonline.battle.utils.VehicleUtils;

public class VehicleWallCollideProcessor {

    public static boolean processCollide(VehicleCalculations vehicle, BattleCalculations battle) {
        if (wallCollide(vehicle, battle)) {
            doCollide(vehicle.getModel());
            vehicle.getModel().setUpdated(true);
            vehicle.getCollisions().add(Collision.WALL);
            return true;
        }
        return false;
    }

    public static boolean checkResolved(VehicleCalculations vehicle, BattleCalculations battle) {
        return !wallCollide(vehicle, battle);
    }

    private static boolean wallCollide(VehicleCalculations vehicle, BattleCalculations battle) {
        var velocityX = vehicle.getModel().getState().getVelocity().getX();
        var wheelRadius = vehicle.getModel().getSpecs().getWheelRadius();
        var xMax = battle.getModel().getRoom().getSpecs().getRightTop().getX();
        var xMin = battle.getModel().getRoom().getSpecs().getLeftBottom().getX();
        if (velocityX > 0) {
            var rightWheelPosition = VehicleUtils.getNextRightWheelPosition(vehicle);
            return rightWheelPosition.getX() + wheelRadius >= xMax;
        }
        if (velocityX < 0) {
            var leftWheelPosition = VehicleUtils.getNextLeftWheelPosition(vehicle);
            return leftWheelPosition.getX() - wheelRadius <= xMin;
        }
        return false;
    }

    private static void doCollide(VehicleModel vehicleModel) {
        vehicleModel.getState().getVelocity().setX(
                - vehicleModel.getState().getVelocity().getX() / 2);
    }
}
