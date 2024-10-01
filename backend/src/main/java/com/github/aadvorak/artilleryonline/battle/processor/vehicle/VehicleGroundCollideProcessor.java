package com.github.aadvorak.artilleryonline.battle.processor.vehicle;

import com.github.aadvorak.artilleryonline.battle.calculations.VehicleCalculations;
import com.github.aadvorak.artilleryonline.battle.model.BattleModel;
import com.github.aadvorak.artilleryonline.battle.model.VehicleModel;
import com.github.aadvorak.artilleryonline.battle.utils.BattleUtils;
import com.github.aadvorak.artilleryonline.battle.utils.VehicleUtils;

public class VehicleGroundCollideProcessor {

    public static boolean processCollide(VehicleCalculations calculations, VehicleModel vehicleModel,
                                         BattleModel battleModel) {
        var groundCollideAngle = getGroundCollideAngle(calculations, vehicleModel, battleModel);
        if (groundCollideAngle != null) {
            doCollide(vehicleModel, groundCollideAngle);
            vehicleModel.setCollided(true);
            battleModel.setUpdated(true);
            return true;
        }
        return false;
    }

    private static Double getGroundCollideAngle(VehicleCalculations calculations, VehicleModel vehicleModel,
                                                BattleModel battleModel) {
        var nextRightWheelPosition = VehicleUtils.getRightWheelPosition(vehicleModel, calculations.getNextPosition(),
                calculations.getNextAngle());
        var nextLeftWheelPosition = VehicleUtils.getLeftWheelPosition(vehicleModel, calculations.getNextPosition(),
                calculations.getNextAngle());
        var rightWheelNearestGroundPoint = BattleUtils.getNearestGroundPosition(nextRightWheelPosition.getX(),
                battleModel.getRoom());
        var leftWheelNearestGroundPoint = BattleUtils.getNearestGroundPosition(nextLeftWheelPosition.getX(),
                battleModel.getRoom());
        if (rightWheelNearestGroundPoint.getY() >= nextRightWheelPosition.getY()
                && calculations.getRightWheel().getNearestGroundPointByX().getY()
                < calculations.getRightWheel().getPosition().getY()) {
            return calculations.getRightWheel().getGroundAngle();
        }
        if (leftWheelNearestGroundPoint.getY() >= nextLeftWheelPosition.getY()
                && calculations.getLeftWheel().getNearestGroundPointByX().getY()
                < calculations.getLeftWheel().getPosition().getY() ) {
            return calculations.getLeftWheel().getGroundAngle();
        }
        return null;
    }

    private static void doCollide(VehicleModel vehicleModel, double angle) {
        var vehicleVelocity = vehicleModel.getState().getVehicleVelocity();
        var velocityVerticalProjection = vehicleVelocity.getX() * Math.sin(angle) + vehicleVelocity.getY() * Math.cos(angle);
        var velocityHorizontalProjection = vehicleVelocity.getX() * Math.cos(angle) + vehicleVelocity.getY() * Math.sin(angle);
        velocityVerticalProjection = - 2 * velocityVerticalProjection;
        vehicleVelocity.setX(velocityHorizontalProjection * Math.cos(angle)
                + velocityVerticalProjection * Math.sin(angle));
        vehicleVelocity.setY(velocityHorizontalProjection * Math.sin(angle)
                + velocityVerticalProjection * Math.cos(angle));
        vehicleVelocity.setAngle(vehicleVelocity.getAngle() / 2);
    }
}
