package com.github.aadvorak.artilleryonline.battle.processor.vehicle;

import com.github.aadvorak.artilleryonline.battle.calculations.VehicleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.WheelCalculations;
import com.github.aadvorak.artilleryonline.battle.model.BattleModel;
import com.github.aadvorak.artilleryonline.battle.model.VehicleModel;
import com.github.aadvorak.artilleryonline.battle.utils.BattleUtils;
import com.github.aadvorak.artilleryonline.battle.utils.VectorUtils;
import com.github.aadvorak.artilleryonline.battle.utils.VehicleUtils;

public class VehicleGroundCollideProcessor {

    public static boolean processCollide(VehicleCalculations calculations, VehicleModel vehicleModel,
                                         BattleModel battleModel) {
        var groundCollideWheel = getGroundCollideWheel(calculations, vehicleModel, battleModel);
        if (groundCollideWheel != null) {
            doCollide(vehicleModel, calculations, groundCollideWheel);
            battleModel.setUpdated(true);
            return true;
        }
        return false;
    }

    private static WheelCalculations getGroundCollideWheel(VehicleCalculations calculations, VehicleModel vehicleModel,
                                                           BattleModel battleModel) {
        var nextRightWheelPosition = VehicleUtils.getRightWheelPosition(vehicleModel, calculations.getNextPosition(),
                calculations.getNextAngle());
        var nextLeftWheelPosition = VehicleUtils.getLeftWheelPosition(vehicleModel, calculations.getNextPosition(),
                calculations.getNextAngle());
        var rightWheelNearestGroundPoint = BattleUtils.getNearestGroundPosition(nextRightWheelPosition.getX(),
                battleModel.getRoom());
        var leftWheelNearestGroundPoint = BattleUtils.getNearestGroundPosition(nextLeftWheelPosition.getX(),
                battleModel.getRoom());
        if (calculations.getRightWheel().getNearestGroundPoint() != null
                && rightWheelNearestGroundPoint.getY() >= nextRightWheelPosition.getY()) {
            return calculations.getRightWheel();
        }
        if (calculations.getLeftWheel().getNearestGroundPoint() != null
                && leftWheelNearestGroundPoint.getY() >= nextLeftWheelPosition.getY()) {
            return calculations.getLeftWheel();
        }
        return null;
    }

    public static void doCollide(VehicleModel vehicleModel, VehicleCalculations calculations,
                                  WheelCalculations wheelCalculations) {
        VehicleUtils.calculateWheelVelocity(vehicleModel, calculations.getRightWheel());
        VehicleUtils.calculateWheelVelocity(vehicleModel, calculations.getLeftWheel());

        var groundAngle = wheelCalculations.getGroundAngle();
        var wheelVelocity = wheelCalculations.getVelocity();
        var velocityVerticalProjection = VectorUtils.getVerticalProjection(wheelVelocity, groundAngle);
        var velocityHorizontalProjection = VectorUtils.getHorizontalProjection(wheelVelocity, groundAngle);

        velocityVerticalProjection += 3.0 * Math.sqrt(Math.abs(velocityVerticalProjection));
        wheelVelocity.setX(VectorUtils.getComponentX(velocityVerticalProjection, velocityHorizontalProjection, groundAngle));
        wheelVelocity.setY(VectorUtils.getComponentY(velocityVerticalProjection, velocityHorizontalProjection, groundAngle));

        var rightWheelVelocity = calculations.getRightWheel().getVelocity();
        var leftWheelVelocity = calculations.getLeftWheel().getVelocity();
        var angle = vehicleModel.getState().getAngle();

        var angleVelocity = Math.abs(angle) < Math.PI / 4
                ? (rightWheelVelocity.getY() - leftWheelVelocity.getY()) / (2.0 * Math.cos(angle))
                : (leftWheelVelocity.getX() - rightWheelVelocity.getX()) / (2.0 * Math.sin(angle));
        var wheelSign = wheelCalculations.getSign().getValue();
        vehicleModel.getState().getVehicleVelocity()
                .setAngle(angleVelocity)
                .setX(wheelVelocity.getX() - wheelSign * angleVelocity * Math.sin(angle))
                .setY(wheelVelocity.getY() + wheelSign * angleVelocity * Math.cos(angle));
    }
}
