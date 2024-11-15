package com.github.aadvorak.artilleryonline.battle.processor.vehicle;

import com.github.aadvorak.artilleryonline.battle.calculations.BattleCalculations;
import com.github.aadvorak.artilleryonline.battle.common.CollideObjectType;
import com.github.aadvorak.artilleryonline.battle.calculations.VehicleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.WheelCalculations;
import com.github.aadvorak.artilleryonline.battle.common.CollideObject;
import com.github.aadvorak.artilleryonline.battle.utils.BattleUtils;
import com.github.aadvorak.artilleryonline.battle.utils.VectorUtils;
import com.github.aadvorak.artilleryonline.battle.utils.VehicleUtils;

public class VehicleGroundCollideProcessor {

    public static boolean processCollide(VehicleCalculations vehicle, BattleCalculations battle) {
        var groundCollideWheel = getGroundCollideWheel(vehicle, battle);
        if (groundCollideWheel != null) {
            doCollide(vehicle, groundCollideWheel);
            vehicle.getModel().setUpdated(true);
            vehicle.getCollisions().add(new CollideObject()
                    .setType(CollideObjectType.GROUND));
            return true;
        }
        return false;
    }

    public static boolean checkResolved(VehicleCalculations vehicle, BattleCalculations battle) {
        var groundCollideWheel = getGroundCollideWheel(vehicle, battle);
        return groundCollideWheel == null;
    }

    private static WheelCalculations getGroundCollideWheel(VehicleCalculations vehicle, BattleCalculations battle) {
        var nextRightWheelPosition = VehicleUtils.getNextRightWheelPosition(vehicle);
        var nextLeftWheelPosition = VehicleUtils.getNextLeftWheelPosition(vehicle);
        var rightWheelNearestGroundPoint = BattleUtils.getNearestGroundPosition(nextRightWheelPosition.getX(),
                battle.getModel().getRoom());
        var leftWheelNearestGroundPoint = BattleUtils.getNearestGroundPosition(nextLeftWheelPosition.getX(),
                battle.getModel().getRoom());
        if (vehicle.getRightWheel().getNearestGroundPoint() != null
                && rightWheelNearestGroundPoint.getY() >= nextRightWheelPosition.getY()) {
            return vehicle.getRightWheel();
        }
        if (vehicle.getLeftWheel().getNearestGroundPoint() != null
                && leftWheelNearestGroundPoint.getY() >= nextLeftWheelPosition.getY()) {
            return vehicle.getLeftWheel();
        }
        return null;
    }

    public static void doCollide(VehicleCalculations vehicle, WheelCalculations wheelCalculations) {
        VehicleUtils.calculateWheelVelocity(vehicle.getModel(), vehicle.getRightWheel());
        VehicleUtils.calculateWheelVelocity(vehicle.getModel(), vehicle.getLeftWheel());

        var groundAngle = wheelCalculations.getGroundAngle();
        var wheelVelocity = wheelCalculations.getVelocity();
        var velocityVerticalProjection = VectorUtils.getVerticalProjection(wheelVelocity, groundAngle);
        var velocityHorizontalProjection = VectorUtils.getHorizontalProjection(wheelVelocity, groundAngle);

        velocityVerticalProjection += 3.0 * Math.sqrt(Math.abs(velocityVerticalProjection));
        wheelVelocity.setX(VectorUtils.getComponentX(velocityVerticalProjection, velocityHorizontalProjection, groundAngle));
        wheelVelocity.setY(VectorUtils.getComponentY(velocityVerticalProjection, velocityHorizontalProjection, groundAngle));

        VehicleUtils.recalculateVehicleVelocityByWheel(vehicle, wheelCalculations);
    }
}
