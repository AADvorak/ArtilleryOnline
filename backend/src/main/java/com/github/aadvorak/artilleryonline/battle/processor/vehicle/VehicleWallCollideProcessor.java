package com.github.aadvorak.artilleryonline.battle.processor.vehicle;

import com.github.aadvorak.artilleryonline.battle.calculations.VehicleCalculations;
import com.github.aadvorak.artilleryonline.battle.model.BattleModel;
import com.github.aadvorak.artilleryonline.battle.model.VehicleModel;
import com.github.aadvorak.artilleryonline.battle.utils.VehicleUtils;

public class VehicleWallCollideProcessor {

    public static boolean processCollide(VehicleCalculations calculations, VehicleModel vehicleModel,
                                         BattleModel battleModel) {
        if (wallCollide(calculations, vehicleModel, battleModel)) {
            doCollide(vehicleModel);
            vehicleModel.setUpdated(true);
            return true;
        }
        return false;
    }

    private static boolean wallCollide(VehicleCalculations calculations, VehicleModel vehicleModel, BattleModel battleModel) {
        var velocityX = vehicleModel.getState().getVelocity().getX();
        var wheelRadius = vehicleModel.getSpecs().getWheelRadius();
        var xMax = battleModel.getRoom().getSpecs().getRightTop().getX();
        var xMin = battleModel.getRoom().getSpecs().getLeftBottom().getX();
        if (velocityX > 0) {
            var rightWheelPosition = VehicleUtils.getRightWheelPosition(vehicleModel, calculations.getNextPosition(),
                    calculations.getNextAngle());
            return rightWheelPosition.getX() + wheelRadius >= xMax;
        }
        if (velocityX < 0) {
            var leftWheelPosition = VehicleUtils.getLeftWheelPosition(vehicleModel, calculations.getNextPosition(),
                    calculations.getNextAngle());
            return leftWheelPosition.getX() - wheelRadius <= xMin;
        }
        return false;
    }

    private static void doCollide(VehicleModel vehicleModel) {
        vehicleModel.getState().getVelocity().setX(
                - vehicleModel.getState().getVelocity().getX() / 2);
    }
}
