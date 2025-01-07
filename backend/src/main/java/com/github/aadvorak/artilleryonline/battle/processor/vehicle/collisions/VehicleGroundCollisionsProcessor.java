package com.github.aadvorak.artilleryonline.battle.processor.vehicle.collisions;

import com.github.aadvorak.artilleryonline.battle.calculations.BattleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculator.wheel.GroundPositionCalculator;
import com.github.aadvorak.artilleryonline.battle.common.CollideObjectType;
import com.github.aadvorak.artilleryonline.battle.calculations.VehicleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.WheelCalculations;
import com.github.aadvorak.artilleryonline.battle.common.Collision;
import com.github.aadvorak.artilleryonline.battle.utils.BattleUtils;
import com.github.aadvorak.artilleryonline.battle.utils.GeometryUtils;
import com.github.aadvorak.artilleryonline.battle.utils.VectorUtils;

public class VehicleGroundCollisionsProcessor {

    public static boolean process(VehicleCalculations vehicle, BattleCalculations battle) {
        calculateNextGroundPositions(vehicle, battle);
        var groundCollideWheel = getGroundCollideWheel(vehicle);
        if (groundCollideWheel != null) {
            doCollide(battle, vehicle, groundCollideWheel);
            vehicle.getModel().setUpdated(true);
            vehicle.getCollisions().add(new Collision()
                    .setType(CollideObjectType.GROUND));
            return true;
        }
        return false;
    }

    public static boolean checkResolved(VehicleCalculations vehicle, BattleCalculations battle) {
        calculateNextGroundPositions(vehicle, battle);
        var groundCollideWheel = getGroundCollideWheel(vehicle);
        return groundCollideWheel == null;
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

    private static WheelCalculations getGroundCollideWheel(VehicleCalculations vehicle) {
        if (vehicle.getRightWheel().getNext().getNearestGroundPointByX().getY()
                >= vehicle.getRightWheel().getNext().getPosition().getY()) {
            return vehicle.getRightWheel();
        }
        if (vehicle.getLeftWheel().getNext().getNearestGroundPointByX().getY()
                >= vehicle.getLeftWheel().getNext().getPosition().getY()) {
            return vehicle.getLeftWheel();
        }
        return null;
    }

    private static void doCollide(BattleCalculations battle, VehicleCalculations vehicle,
                                  WheelCalculations wheelCalculations) {
        recalculateVehicleVelocity(vehicle, wheelCalculations);
        recalculateVehiclePosition(battle, vehicle, wheelCalculations);
    }

    private static void recalculateVehicleVelocity(VehicleCalculations vehicle, WheelCalculations wheelCalculations) {
        vehicle.recalculateWheelsVelocities();

        var groundAngle = wheelCalculations.getGroundAngle();
        var wheelVelocity = wheelCalculations.getVelocity();
        var velocityVerticalProjection = - 0.5 * VectorUtils.getVerticalProjection(wheelVelocity, groundAngle);
        var velocityHorizontalProjection = VectorUtils.getHorizontalProjection(wheelVelocity, groundAngle);

        wheelVelocity.setX(VectorUtils.getComponentX(velocityVerticalProjection, velocityHorizontalProjection, groundAngle));
        wheelVelocity.setY(VectorUtils.getComponentY(velocityVerticalProjection, velocityHorizontalProjection, groundAngle));

        vehicle.recalculateVelocityByWheel(wheelCalculations);
    }

    private static void recalculateVehiclePosition(BattleCalculations battle, VehicleCalculations vehicle,
                                                   WheelCalculations wheelCalculations) {
        var nearestGroundPoint = GroundPositionCalculator.getNearestGroundPoint(
                wheelCalculations.getNext().getPosition(),
                vehicle.getModel().getSpecs().getWheelRadius(),
                battle.getModel().getRoom(),
                wheelCalculations.getSign().getValue()
        );
        if (nearestGroundPoint != null) {
            var groundAngle = GroundPositionCalculator.getGroundAngle(
                    wheelCalculations.getNext().getPosition(),
                    nearestGroundPoint,
                    battle.getModel().getRoom()
            );
            var normalMove = nearestGroundPoint.distance();
            GeometryUtils.applyNormalMoveToPosition(vehicle.getNextPosition(), normalMove, groundAngle);
        } else {
            var yMove = wheelCalculations.getNext().getNearestGroundPointByX().getY()
                    - wheelCalculations.getNext().getPosition().getY();
            vehicle.getNextPosition().setY(vehicle.getNextPosition().getY() + yMove);
        }
    }
}
