package com.github.aadvorak.artilleryonline.battle.processor.vehicle.collisions;

import com.github.aadvorak.artilleryonline.battle.calculations.BattleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.VehicleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.WheelCalculations;
import com.github.aadvorak.artilleryonline.battle.common.Collision;
import com.github.aadvorak.artilleryonline.battle.utils.BattleUtils;
import com.github.aadvorak.artilleryonline.battle.utils.VectorUtils;

public class VehicleGroundCollisionsProcessor {

    public static boolean process(VehicleCalculations vehicle, BattleCalculations battle) {
        calculateNextGroundPositions(vehicle, battle);
        var collision = VehicleGroundCollisionsDetector.detectFirst(vehicle, battle);
        if (collision != null) {
            resolve(collision, battle);
            vehicle.getModel().setUpdated(true);
            vehicle.getCollisions().add(collision);
            return true;
        }
        return false;
    }

    public static boolean checkResolved(VehicleCalculations vehicle, BattleCalculations battle) {
        calculateNextGroundPositions(vehicle, battle);
        var collision = VehicleGroundCollisionsDetector.detectFirst(vehicle, battle);
        return collision == null;
    }

    private static void calculateNextGroundPositions(VehicleCalculations vehicle, BattleCalculations battle) {
        vehicle.getRightWheel().getNext().setNearestGroundPointByX(BattleUtils.getNearestGroundPosition(
                vehicle.getRightWheel().getNext().getPosition().getX(),
                battle.getModel().getRoom()
        ));
        vehicle.getLeftWheel().getNext().setNearestGroundPointByX(BattleUtils.getNearestGroundPosition(
                vehicle.getLeftWheel().getNext().getPosition().getX(),
                battle.getModel().getRoom()
        ));
    }

    private static void resolve(Collision collision, BattleCalculations battle) {
        if (collision.getPair().first() instanceof WheelCalculations wheel) {
            recalculateVehicleVelocity(wheel);
            collision.getPair().first().getVehicleCalculations()
                    .calculateNextPositionAndAngle(battle.getModel().getCurrentTimeStepSecs());
        }
        recalculateVehiclePosition(collision);
    }

    private static void recalculateVehicleVelocity(WheelCalculations wheel) {
        wheel.getVehicle().recalculateWheelsVelocities();

        var groundAngle = wheel.getGroundAngle();
        var wheelVelocity = wheel.getVelocity();
        var velocityVerticalProjection = - VectorUtils.getVerticalProjection(wheelVelocity, groundAngle);
        var velocityHorizontalProjection = VectorUtils.getHorizontalProjection(wheelVelocity, groundAngle);

        wheelVelocity.setX(VectorUtils.getComponentX(velocityVerticalProjection, velocityHorizontalProjection, groundAngle));
        wheelVelocity.setY(VectorUtils.getComponentY(velocityVerticalProjection, velocityHorizontalProjection, groundAngle));

        wheel.getVehicle().recalculateVelocityByWheel(wheel);
    }

    private static void recalculateVehiclePosition(Collision collision) {
        collision.getPair().first().getVehicleCalculations()
                .applyNormalMoveToNextPosition(2.0 * collision.getInterpenetration(), collision.getAngle());
    }
}
