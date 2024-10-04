package com.github.aadvorak.artilleryonline.battle.calculator;

import com.github.aadvorak.artilleryonline.battle.calculations.NearestGroundPoint;
import com.github.aadvorak.artilleryonline.battle.calculations.VehicleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.WheelCalculations;
import com.github.aadvorak.artilleryonline.battle.calculator.wheel.*;
import com.github.aadvorak.artilleryonline.battle.common.*;
import com.github.aadvorak.artilleryonline.battle.model.RoomModel;
import com.github.aadvorak.artilleryonline.battle.model.VehicleModel;
import com.github.aadvorak.artilleryonline.battle.utils.BattleUtils;
import com.github.aadvorak.artilleryonline.battle.utils.VehicleUtils;

public class VehicleAccelerationCalculator {

    public static VehicleAcceleration getVehicleAcceleration(VehicleCalculations calculations,
                                                             VehicleModel vehicleModel, RoomModel roomModel) {
        var angle = vehicleModel.getState().getAngle();

        VehicleUtils.calculateWheelVelocity(vehicleModel, calculations.getRightWheel());
        VehicleUtils.calculateWheelVelocity(vehicleModel, calculations.getLeftWheel());

        calculations.getRightWheel().setPosition(VehicleUtils.getRightWheelPosition(vehicleModel));
        calculations.getLeftWheel().setPosition(VehicleUtils.getLeftWheelPosition(vehicleModel));

        calculateWheelAcceleration(calculations.getRightWheel(), vehicleModel, roomModel);
        calculateWheelAcceleration(calculations.getLeftWheel(), vehicleModel, roomModel);

        var rotatingAcceleration = getVehicleRotatingAcceleration(calculations, angle);
        var movingAcceleration = new Acceleration()
                .setX((calculations.getRightWheel().getSumAcceleration().getX()
                        + calculations.getLeftWheel().getSumAcceleration().getX()) / 2)
                .setY((calculations.getRightWheel().getSumAcceleration().getY()
                        + calculations.getLeftWheel().getSumAcceleration().getY()) / 2);

        var vehicleVelocity = vehicleModel.getState().getVelocity();
        var frictionCoefficient = vehicleModel.getPreCalc().getFrictionCoefficient();
        var frictionAcceleration = new Acceleration()
                .setX( - vehicleVelocity.getX() * Math.abs(vehicleVelocity.getX()) * frictionCoefficient)
                .setY( - vehicleVelocity.getY() * Math.abs(vehicleVelocity.getY()) * frictionCoefficient);

        return new VehicleAcceleration()
                .setMovingAcceleration(Acceleration.sumOf(
                        movingAcceleration,
                        frictionAcceleration
                ))
                .setAngle(rotatingAcceleration / vehicleModel.getSpecs().getRadius()
                        - vehicleVelocity.getAngle());
    }

    private static double getVehicleRotatingAcceleration(VehicleCalculations calculations, double angle) {
        var rightWheelRotatingAcceleration = calculations.getRightWheel().getSumAcceleration().getX() * Math.sin(angle)
                + calculations.getRightWheel().getSumAcceleration().getY() * Math.cos(angle);
        var leftWheelRotatingAcceleration = calculations.getLeftWheel().getSumAcceleration().getX() * Math.sin(angle)
                + calculations.getLeftWheel().getSumAcceleration().getY() * Math.cos(angle);
        return (rightWheelRotatingAcceleration - leftWheelRotatingAcceleration) / 2;
    }

    private static void calculateWheelAcceleration(WheelCalculations wheelCalculations,
                                                   VehicleModel vehicleModel, RoomModel roomModel) {
        var roomGravityAcceleration = roomModel.getSpecs().getGravityAcceleration();
        var groundReactionCoefficient = roomModel.getSpecs().getGroundReactionCoefficient();
        var groundFrictionCoefficient = roomModel.getSpecs().getGroundFrictionCoefficient();
        var groundMaxDepth = roomModel.getSpecs().getGroundMaxDepth();
        var wheelRadius = vehicleModel.getSpecs().getWheelRadius();

        wheelCalculations.setNearestGroundPointByX(BattleUtils.getNearestGroundPosition(
                wheelCalculations.getPosition().getX(), roomModel));
        calculateNearestGroundPointAngleAndDepth(wheelCalculations, wheelRadius, roomModel);
        GroundStateCalculator.calculate(wheelCalculations);

        EngineAccelerationCalculator.calculate(wheelCalculations, vehicleModel);
        GroundFrictionAccelerationCalculator.calculate(wheelCalculations, vehicleModel, groundFrictionCoefficient);
        GroundReactionAccelerationCalculator.calculate(wheelCalculations, groundReactionCoefficient);
        GravityAccelerationCalculator.calculate(wheelCalculations, roomGravityAcceleration, groundMaxDepth);
    }

    private static void calculateNearestGroundPointAngleAndDepth(WheelCalculations wheelCalculations,
                                                                 double wheelRadius, RoomModel roomModel) {
        var nearestGroundPoint = getNearestGroundPoint(wheelCalculations.getPosition(), wheelRadius,
                roomModel, wheelCalculations.getSign().getValue());
        if (nearestGroundPoint == null) {
            return;
        }
        wheelCalculations.setNearestGroundPoint(nearestGroundPoint);
        wheelCalculations.setGroundAngle(getGroundAngle(wheelCalculations.getPosition(), nearestGroundPoint, roomModel));
        if (nearestGroundPoint.position().getY() <= wheelCalculations.getPosition().getY()) {
            wheelCalculations.setDepth(wheelRadius - nearestGroundPoint.distance());
        } else {
            wheelCalculations.setDepth(wheelRadius + nearestGroundPoint.distance());
        }
    }

    private static double getGroundAngle(Position position, NearestGroundPoint nearestGroundPoint, RoomModel roomModel) {
        if (nearestGroundPoint.index() > 0 && nearestGroundPoint.position().getX() <= position.getX()) {
            var otherGroundPosition = BattleUtils.getGroundPosition(nearestGroundPoint.index() - 1, roomModel);
            return Math.atan((nearestGroundPoint.position().getY() - otherGroundPosition.getY())
                    / (nearestGroundPoint.position().getX() - otherGroundPosition.getX()));
        } else {
            var otherGroundPosition = BattleUtils.getGroundPosition(nearestGroundPoint.index() + 1, roomModel);
            return Math.atan((otherGroundPosition.getY() - nearestGroundPoint.position().getY())
                    / (otherGroundPosition.getX() - nearestGroundPoint.position().getX()));
        }
    }

    private static NearestGroundPoint getNearestGroundPoint(Position objectPosition, double objectRadius,
                                                            RoomModel roomModel, int sign) {
        var groundIndexes = BattleUtils.getGroundIndexesBetween(objectPosition.getX() - objectRadius,
                objectPosition.getX() + objectRadius, roomModel);
        if (groundIndexes.isEmpty()) {
            return null;
        }
        Position nearestPosition = null;
        Double minimalDistance = null;
        Integer index = null;
        var i = sign > 0 ? 0 : groundIndexes.size() - 1;
        while (i >= 0 && i < groundIndexes.size()) {
            var position = BattleUtils.getGroundPosition(groundIndexes.get(i), roomModel);
            var distance = position.distanceTo(objectPosition);
            if (distance <= objectRadius) {
                if (minimalDistance == null || distance < minimalDistance) {
                    nearestPosition = position;
                    minimalDistance = distance;
                    index = groundIndexes.get(i);
                }
            }
            i += sign;
        }
        if (nearestPosition == null) {
            return null;
        }
        return new NearestGroundPoint(nearestPosition, minimalDistance, index);
    }
}
