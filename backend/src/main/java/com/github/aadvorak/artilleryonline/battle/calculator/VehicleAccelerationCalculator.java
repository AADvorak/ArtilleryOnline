package com.github.aadvorak.artilleryonline.battle.calculator;

import com.github.aadvorak.artilleryonline.battle.calculations.NearestGroundPoint;
import com.github.aadvorak.artilleryonline.battle.calculations.VehicleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.WheelCalculations;
import com.github.aadvorak.artilleryonline.battle.common.*;
import com.github.aadvorak.artilleryonline.battle.model.RoomModel;
import com.github.aadvorak.artilleryonline.battle.model.VehicleModel;
import com.github.aadvorak.artilleryonline.battle.utils.BattleUtils;
import com.github.aadvorak.artilleryonline.battle.utils.VectorUtils;
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

        var vehicleVelocity = vehicleModel.getState().getVehicleVelocity();
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
        var wheelRadius = vehicleModel.getSpecs().getWheelRadius();

        wheelCalculations.setNearestGroundPointByX(BattleUtils.getNearestGroundPosition(
                wheelCalculations.getPosition().getX(), roomModel));
        calculateNearestGroundPointAngleAndDepth(wheelCalculations, wheelRadius, roomModel);

        if (wheelCalculations.getNearestGroundPointByX().getY() >= wheelCalculations.getPosition().getY()) {
            var depth = wheelCalculations.getDepth() != null ? wheelCalculations.getDepth() : 2 * wheelRadius;
            wheelCalculations.setGroundFrictionAcceleration(getInGroundFrictionAcceleration(
                    wheelCalculations.getVelocity(), depth, groundFrictionCoefficient));
            if (wheelCalculations.getNearestGroundPoint() != null) {
                wheelCalculations.setGroundReactionAcceleration(getGroundReactionAcceleration(
                        wheelCalculations.getVelocity(), wheelCalculations.getGroundAngle(), depth,
                        groundReactionCoefficient));
            }
            return;
        }

        if (wheelCalculations.getNearestGroundPoint() == null) {
            wheelCalculations.getGravityAcceleration().setX(0).setY(-roomGravityAcceleration);
            return;
        }

        if (wheelCalculations.getDepth() <= roomModel.getSpecs().getGroundMaxDepth()) {
            wheelCalculations.setGravityAcceleration(getOnGroundGravityAcceleration(roomGravityAcceleration,
                    wheelCalculations.getGroundAngle()));
        }
        wheelCalculations.setGroundReactionAcceleration(getGroundReactionAcceleration(wheelCalculations.getVelocity(),
                wheelCalculations.getGroundAngle(), wheelCalculations.getDepth(), groundReactionCoefficient));
        wheelCalculations.setGroundFrictionAcceleration(getInGroundFrictionAcceleration(wheelCalculations.getVelocity(),
                wheelCalculations.getDepth(), groundFrictionCoefficient));
        wheelCalculations.setEngineAcceleration(getWheelEngineAcceleration(vehicleModel,
                wheelCalculations.getGroundAngle(), wheelCalculations.getDepth()));
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

    private static Acceleration getOnGroundGravityAcceleration(double roomGravityAcceleration, double groundAngle) {
        var groundAccelerationModule = Math.abs(roomGravityAcceleration * Math.sin(groundAngle));
        return new Acceleration()
                .setX(-groundAccelerationModule * Math.sin(groundAngle))
                .setY(-groundAccelerationModule * Math.cos(groundAngle));
    }

    private static Acceleration getGroundReactionAcceleration(Velocity velocity, double groundAngle,
                                                              double depth, double coefficient) {
        var velocityVerticalProjection = VectorUtils.getVerticalProjection(velocity, groundAngle);
        if (velocityVerticalProjection >= 0) {
            return new Acceleration();
        } else {
            var accelerationVerticalProjection = - velocityVerticalProjection * depth * coefficient;
            return new Acceleration()
                    .setX(VectorUtils.getComponentX(accelerationVerticalProjection, 0.0, groundAngle))
                    .setY(VectorUtils.getComponentY(accelerationVerticalProjection, 0.0, groundAngle));
        }
    }

    private static Acceleration getInGroundFrictionAcceleration(Velocity velocity, double depth, double coefficient) {
        return new Acceleration()
                .setX( - velocity.getX() * depth * coefficient)
                .setY( - velocity.getY() * depth * coefficient);
    }

    private static Acceleration getWheelEngineAcceleration(VehicleModel vehicleModel, double angle, double depth) {
        if (vehicleModel.getState().getTrackState().isBroken()) {
            return new Acceleration();
        }
        var direction = vehicleModel.getState().getMovingDirection();
        var wheelRadius = vehicleModel.getSpecs().getWheelRadius();
        var depthCoefficient = 1 - depth * 0.5 / wheelRadius;
        var acceleration = depthCoefficient * vehicleModel.getSpecs().getAcceleration() / 2;
        var depthAngle = depth * Math.PI / (4 * wheelRadius);
        if (MovingDirection.RIGHT.equals(direction)) {
            return new Acceleration()
                    .setX(acceleration * Math.cos(angle + depthAngle))
                    .setY(acceleration * Math.sin(angle + depthAngle));
        }
        if (MovingDirection.LEFT.equals(direction)) {
            return new Acceleration()
                    .setX( - acceleration * Math.cos(angle - depthAngle))
                    .setY( - acceleration * Math.sin(angle - depthAngle));
        }
        return new Acceleration();
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
