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
                .setX((rightWheelAcceleration.getX() + leftWheelAcceleration.getX()) / 2)
                .setY((rightWheelAcceleration.getY() + leftWheelAcceleration.getY()) / 2)
                .setAngle(rotatingAcceleration / vehicleModel.getSpecs().getRadius());
    }

    private static Acceleration getWheelAcceleration(Position wheelPosition, Velocity wheelVelocity,
                                                     VehicleModel vehicleModel, RoomModel roomModel,
                                                     int sign) {
        var roomGravityAcceleration = roomModel.getSpecs().getGravityAcceleration();
        var wheelRadius = vehicleModel.getSpecs().getWheelRadius();
        var nearestGroundPositionByX = BattleUtils.getNearestGroundPosition(wheelPosition.getX(), roomModel);
        if (nearestGroundPositionByX.getY() >= wheelPosition.getY()) {
            var frictionAcceleration = getWheelFrictionAcceleration(wheelVelocity, wheelRadius);
            var engineAcceleration = getWheelEngineAcceleration(vehicleModel,
                    getWheelUnderGroundMovingAngle(vehicleModel));
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
        var groundAcceleration = getWheelGroundAcceleration(roomGravityAcceleration, groundAngle, depth);
        var frictionAcceleration = getWheelFrictionAcceleration(wheelVelocity, depth);
        var engineAcceleration = getWheelEngineAcceleration(vehicleModel, groundAngle);
        return Acceleration.sumOf(List.of(
                groundAcceleration,
                frictionAcceleration,
                engineAcceleration
        ));
    }

    private static Acceleration getWheelGroundAcceleration(double roomGravityAcceleration, double groundAngle,
                                                           double depth) {
        var groundAccelerationModule = Math.abs(roomGravityAcceleration * Math.sin(groundAngle)) * (1 + depth);
        return new Acceleration()
                .setX(-groundAccelerationModule * Math.sin(groundAngle))
                .setY(groundAccelerationModule * Math.cos(groundAngle));
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

    private static Acceleration getWheelFrictionAcceleration(Velocity velocity, double depth) {
        var coefficient = 200;
        return new Acceleration()
                .setX( - velocity.getX() * depth * coefficient)
                .setY( - velocity.getY() * depth * coefficient);
    }

    private static double getWheelUnderGroundMovingAngle(VehicleModel vehicleModel) {
        var direction = vehicleModel.getState().getMovingDirection();
        if (MovingDirection.RIGHT.equals(direction)) {
            return Math.PI / 4;
        }
        if (MovingDirection.LEFT.equals(direction)) {
            return - Math.PI / 4;
        }
        return 0.0;
    }

    private static Acceleration getWheelEngineAcceleration(VehicleModel vehicleModel, double angle) {
        if (vehicleModel.getState().getTrackState().isBroken()) {
            return new Acceleration();
        }
        var direction = vehicleModel.getState().getMovingDirection();
        var acceleration = vehicleModel.getSpecs().getAcceleration() / 2;
        if (MovingDirection.RIGHT.equals(direction)) {
            return new Acceleration()
                    .setX(acceleration * Math.cos(angle))
                    .setY(acceleration * Math.sin(angle));
        }
        if (MovingDirection.LEFT.equals(direction)) {
            return new Acceleration()
                    .setX( - acceleration * Math.cos(angle))
                    .setY( - acceleration * Math.sin(angle));
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
