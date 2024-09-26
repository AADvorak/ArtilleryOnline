package com.github.aadvorak.artilleryonline.battle.utils;

import com.github.aadvorak.artilleryonline.battle.common.*;
import com.github.aadvorak.artilleryonline.battle.model.RoomModel;
import com.github.aadvorak.artilleryonline.battle.model.VehicleModel;

import java.util.List;

public class VehicleAccelerationUtils {

    public static VehicleAcceleration getVehicleAcceleration(VehicleModel vehicleModel, RoomModel roomModel) {
        var angle = vehicleModel.getState().getAngle();
        var rightWheelVelocity = getWheelVelocity(vehicleModel, -1);
        var leftWheelVelocity = getWheelVelocity(vehicleModel, 1);
        var vehicleVelocity = vehicleModel.getState().getVehicleVelocity();
        var frictionCoefficient = vehicleModel.getPreCalc().getFrictionCoefficient();
        var frictionAcceleration = new Acceleration()
                .setX( - vehicleVelocity.getX() * frictionCoefficient)
                .setY( - vehicleVelocity.getY() * frictionCoefficient);
        var rightWheelAcceleration = getWheelAcceleration(VehicleUtils.getRightWheelPosition(vehicleModel),
                rightWheelVelocity, vehicleModel, roomModel, -1);
        var leftWheelAcceleration = getWheelAcceleration(VehicleUtils.getLeftWheelPosition(vehicleModel),
                leftWheelVelocity, vehicleModel, roomModel, 1);
        var rightWheelRotatingAcceleration = rightWheelAcceleration.getX() * Math.sin(angle)
                + rightWheelAcceleration.getY() * Math.cos(angle);
        var leftWheelRotatingAcceleration = leftWheelAcceleration.getX() * Math.sin(angle)
                + leftWheelAcceleration.getY() * Math.cos(angle);
        var rotatingAcceleration = (rightWheelRotatingAcceleration - leftWheelRotatingAcceleration) / 2;
        return new VehicleAcceleration()
                .setX((rightWheelAcceleration.getX() + leftWheelAcceleration.getX()) / 2 + frictionAcceleration.getX())
                .setY((rightWheelAcceleration.getY() + leftWheelAcceleration.getY()) / 2 + frictionAcceleration.getY())
                .setAngle(rotatingAcceleration / vehicleModel.getSpecs().getRadius());
    }

    private static Acceleration getWheelAcceleration(Position wheelPosition, Velocity wheelVelocity,
                                                     VehicleModel vehicleModel, RoomModel roomModel,
                                                     int sign) {
        var roomGravityAcceleration = roomModel.getSpecs().getGravityAcceleration();
        var groundReactionCoefficient = roomModel.getSpecs().getGroundReactionCoefficient();
        var wheelRadius = vehicleModel.getSpecs().getWheelRadius();
        var nearestGroundPositionByX = BattleUtils.getNearestGroundPosition(wheelPosition.getX(), roomModel);
        if (nearestGroundPositionByX.getY() >= wheelPosition.getY()) {
            var frictionAcceleration = getWheelFrictionAcceleration(wheelVelocity, wheelRadius, groundReactionCoefficient);
            var engineAcceleration = getWheelEngineAcceleration(vehicleModel, 0.0, wheelRadius, wheelRadius);
            return Acceleration.sumOf(List.of(
                    frictionAcceleration,
                    engineAcceleration
            ));
        }
        var nearestGroundPosition = getWheelNearestGroundPosition(wheelPosition,
                vehicleModel.getSpecs().getWheelRadius(), roomModel, sign);
        if (nearestGroundPosition == null) {
            return new Acceleration().setX(0).setY(-roomGravityAcceleration);
        }
        var groundAngle = Math.atan((nearestGroundPosition.position().getX() - wheelPosition.getX())
                / Math.abs(wheelPosition.getY() - nearestGroundPosition.position().getY()));
        var depth = wheelRadius - nearestGroundPosition.distance();
        var groundAcceleration = depth <= roomModel.getSpecs().getGroundMaxDepth()
                ? getWheelGroundAcceleration(roomGravityAcceleration, groundAngle)
                : new Acceleration();
        var groundReactionAcceleration = getWheelGroundReactionAcceleration(wheelVelocity, groundAngle,
                depth, groundReactionCoefficient);
        var engineAcceleration = getWheelEngineAcceleration(vehicleModel, groundAngle, depth, wheelRadius);
        return Acceleration.sumOf(List.of(
                groundAcceleration,
                groundReactionAcceleration,
                engineAcceleration
        ));
    }

    private static Acceleration getWheelGroundAcceleration(double roomGravityAcceleration, double groundAngle) {
        var groundAccelerationModule = Math.abs(roomGravityAcceleration * Math.sin(groundAngle));
        return new Acceleration()
                .setX(-groundAccelerationModule * Math.sin(groundAngle))
                .setY(-groundAccelerationModule * Math.cos(groundAngle));
    }

    private static Acceleration getWheelGroundReactionAcceleration(Velocity velocity, double groundAngle,
                                                                   double depth, double coefficient) {
        var velocityAxialProjection = velocity.getX() * Math.sin(groundAngle) + velocity.getY() * Math.cos(groundAngle);
        if (velocityAxialProjection >= 0) {
            return new Acceleration();
        } else {
            var accelerationModule = - velocityAxialProjection * depth * coefficient;
            return new Acceleration()
                    .setX(accelerationModule * Math.sin(groundAngle))
                    .setY(accelerationModule * Math.cos(groundAngle));
        }
    }

    private static Velocity getWheelVelocity(VehicleModel vehicleModel, int sign) {
        var vehicleVelocity = vehicleModel.getState().getVehicleVelocity();
        var angle = vehicleModel.getState().getAngle();
        var angleVelocity = vehicleVelocity.getAngle() * vehicleModel.getSpecs().getRadius();
        var velocityX = vehicleVelocity.getX() + sign * angleVelocity * Math.sin(angle);
        var velocityY = vehicleVelocity.getY() - sign * angleVelocity * Math.cos(angle);
        return new Velocity()
                .setX(velocityX)
                .setY(velocityY);
    }

    /**
     * todo to getWheelGroundReactionAcceleration
     */
    private static Acceleration getWheelFrictionAcceleration(Velocity velocity, double depth, double coefficient) {
        return new Acceleration()
                .setX( - velocity.getX() * depth * coefficient)
                .setY( - velocity.getY() * depth * coefficient);
    }

    private static Acceleration getWheelEngineAcceleration(VehicleModel vehicleModel, double angle,
                                                           double depth, double wheelRadius) {
        if (vehicleModel.getState().getTrackState().isBroken()) {
            return new Acceleration();
        }
        var direction = vehicleModel.getState().getMovingDirection();
        var depthCoefficient = 1 - depth * 0.4 / wheelRadius;
        var acceleration = depthCoefficient * vehicleModel.getSpecs().getAcceleration() / 2;
        var depthAngle = depth * Math.PI / (4 * vehicleModel.getSpecs().getWheelRadius());
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

    private static PositionAndDistance getWheelNearestGroundPosition(Position wheelPosition, double wheelRadius,
                                                                     RoomModel roomModel, int sign) {
        var groundIndexes = BattleUtils.getGroundIndexesBetween(wheelPosition.getX() - wheelRadius,
                wheelPosition.getX() + wheelRadius, roomModel);
        if (groundIndexes.isEmpty()) {
            return null;
        }
        Position nearestPosition = null;
        Double minimalDistance = null;
        var i = sign > 0 ? 0 : groundIndexes.size() - 1;
        while (i >= 0 && i < groundIndexes.size()) {
            var position = BattleUtils.getGroundPosition(groundIndexes.get(i), roomModel);
            var distance = position.distanceTo(wheelPosition);
            if (distance <= wheelRadius) {
                if (minimalDistance == null || distance < minimalDistance) {
                    nearestPosition = position;
                    minimalDistance = distance;
                }
            }
            i += sign;
        }
        if (nearestPosition == null) {
            return null;
        }
        return new PositionAndDistance(nearestPosition, minimalDistance);
    }

    private record PositionAndDistance(Position position, Double distance) {}
}
