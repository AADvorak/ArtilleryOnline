package com.github.aadvorak.artilleryonline.battle.processor.vehicle.collisions;

import com.github.aadvorak.artilleryonline.battle.calculations.BattleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculator.wheel.GroundPositionCalculator;
import com.github.aadvorak.artilleryonline.battle.common.CollideObjectType;
import com.github.aadvorak.artilleryonline.battle.calculations.VehicleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.WheelCalculations;
import com.github.aadvorak.artilleryonline.battle.common.CollideObject;
import com.github.aadvorak.artilleryonline.battle.utils.BattleUtils;
import com.github.aadvorak.artilleryonline.battle.utils.VectorUtils;
import com.github.aadvorak.artilleryonline.battle.utils.VehicleUtils;

public class VehicleGroundCollideProcessor {

    public static boolean processCollide(VehicleCalculations vehicle, BattleCalculations battle) {
        calculateNextWheelAndGroundPositions(vehicle, battle);
        var groundCollideWheel = getGroundCollideWheel(vehicle);
        if (groundCollideWheel != null) {
            doCollide(battle, vehicle, groundCollideWheel);
            vehicle.getModel().setUpdated(true);
            vehicle.getCollisions().add(new CollideObject()
                    .setType(CollideObjectType.GROUND));
            return true;
        }
        return false;
    }

    public static boolean checkResolved(VehicleCalculations vehicle, BattleCalculations battle) {
        calculateNextWheelAndGroundPositions(vehicle, battle);
        var groundCollideWheel = getGroundCollideWheel(vehicle);
        return groundCollideWheel == null;
    }

    private static void calculateNextWheelAndGroundPositions(VehicleCalculations vehicle, BattleCalculations battle) {
        vehicle.getRightWheel().getNext().setPosition(VehicleUtils.getNextRightWheelPosition(vehicle));
        vehicle.getLeftWheel().getNext().setPosition(VehicleUtils.getNextLeftWheelPosition(vehicle));
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
        VehicleUtils.calculateWheelVelocity(vehicle.getModel(), vehicle.getRightWheel());
        VehicleUtils.calculateWheelVelocity(vehicle.getModel(), vehicle.getLeftWheel());

        var groundAngle = wheelCalculations.getGroundAngle();
        var wheelVelocity = wheelCalculations.getVelocity();
        var velocityVerticalProjection = VectorUtils.getVerticalProjection(wheelVelocity, groundAngle);
        var velocityHorizontalProjection = VectorUtils.getHorizontalProjection(wheelVelocity, groundAngle);

        velocityVerticalProjection = - velocityVerticalProjection;
        wheelVelocity.setX(VectorUtils.getComponentX(velocityVerticalProjection, velocityHorizontalProjection, groundAngle));
        wheelVelocity.setY(VectorUtils.getComponentY(velocityVerticalProjection, velocityHorizontalProjection, groundAngle));

        VehicleUtils.recalculateVehicleVelocityByWheel(vehicle, wheelCalculations);
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
            var position = vehicle.getPosition();
            var normalProjection = VectorUtils.getVerticalProjection(position, groundAngle);
            var tangentialProjection = VectorUtils.getHorizontalProjection(position, groundAngle);
            normalProjection += normalMove;
            position.setX(VectorUtils.getComponentX(normalProjection, tangentialProjection, groundAngle));
            position.setY(VectorUtils.getComponentY(normalProjection, tangentialProjection, groundAngle));
        } else {
            var yMove = wheelCalculations.getNext().getNearestGroundPointByX().getY()
                    - wheelCalculations.getNext().getPosition().getY();
            vehicle.getPosition().setY(wheelCalculations.getPosition().getY() + yMove);
        }
    }
}
