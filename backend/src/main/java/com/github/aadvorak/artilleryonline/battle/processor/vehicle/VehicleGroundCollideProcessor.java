package com.github.aadvorak.artilleryonline.battle.processor.vehicle;

import com.github.aadvorak.artilleryonline.battle.common.Position;
import com.github.aadvorak.artilleryonline.battle.model.BattleModel;
import com.github.aadvorak.artilleryonline.battle.model.VehicleModel;
import com.github.aadvorak.artilleryonline.battle.utils.BattleUtils;
import com.github.aadvorak.artilleryonline.battle.utils.VehicleUtils;

public class VehicleGroundCollideProcessor {

    public static boolean processCollide(VehicleModel vehicleModel, BattleModel battleModel,
                                         Position nextPosition, double nextAngle) {
        if (groundCollide(vehicleModel, battleModel, nextPosition, nextAngle)) {
            doCollide(vehicleModel);
            battleModel.setUpdated(true);
            return true;
        }
        return false;
    }

    private static boolean groundCollide(VehicleModel vehicleModel, BattleModel battleModel,
                                         Position nextPosition, double nextAngle) {
        var nextRightWheelPosition = VehicleUtils.getRightWheelPosition(vehicleModel, nextPosition, nextAngle);
        var nextLeftWheelPosition = VehicleUtils.getLeftWheelPosition(vehicleModel, nextPosition, nextAngle);
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
