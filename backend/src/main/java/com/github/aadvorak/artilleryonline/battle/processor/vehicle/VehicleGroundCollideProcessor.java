package com.github.aadvorak.artilleryonline.battle.processor.vehicle;

import com.github.aadvorak.artilleryonline.battle.calculations.VehicleCalculations;
import com.github.aadvorak.artilleryonline.battle.model.BattleModel;
import com.github.aadvorak.artilleryonline.battle.model.VehicleModel;
import com.github.aadvorak.artilleryonline.battle.utils.BattleUtils;
import com.github.aadvorak.artilleryonline.battle.utils.VehicleUtils;

public class VehicleGroundCollideProcessor {

    public static boolean processCollide(VehicleCalculations calculations, VehicleModel vehicleModel,
                                         BattleModel battleModel) {
        if (groundCollide(calculations, vehicleModel, battleModel)) {
            doCollide(vehicleModel);
            battleModel.setUpdated(true);
            return true;
        }
        return false;
    }

    private static boolean groundCollide(VehicleCalculations calculations, VehicleModel vehicleModel,
                                         BattleModel battleModel) {
        var nextRightWheelPosition = VehicleUtils.getRightWheelPosition(vehicleModel, calculations.getNextPosition(),
                calculations.getNextAngle());
        var nextLeftWheelPosition = VehicleUtils.getLeftWheelPosition(vehicleModel, calculations.getNextPosition(),
                calculations.getNextAngle());
        var rightWheelNearestGroundPoint = BattleUtils.getNearestGroundPosition(nextRightWheelPosition.getX(),
                battleModel.getRoom());
        var leftWheelNearestGroundPoint = BattleUtils.getNearestGroundPosition(nextLeftWheelPosition.getX(),
                battleModel.getRoom());
        return rightWheelNearestGroundPoint.getY() >= nextRightWheelPosition.getY()
                || leftWheelNearestGroundPoint.getY() >= nextLeftWheelPosition.getY();
    }

    private static void doCollide(VehicleModel vehicleModel) {
        vehicleModel.getState().getVehicleVelocity().setX(
                - vehicleModel.getState().getVehicleVelocity().getX() / 2);
        if (vehicleModel.getState().getVehicleVelocity().getY() < 0) {
            vehicleModel.getState().getVehicleVelocity().setY(
                    - vehicleModel.getState().getVehicleVelocity().getY() / 2);
        }
    }
}
