package com.github.aadvorak.artilleryonline.battle.processor.vehicle;

import com.github.aadvorak.artilleryonline.battle.calculations.VehicleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.WheelCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.WheelSign;
import com.github.aadvorak.artilleryonline.battle.common.Position;
import com.github.aadvorak.artilleryonline.battle.model.BattleModel;
import com.github.aadvorak.artilleryonline.battle.model.VehicleModel;
import com.github.aadvorak.artilleryonline.battle.utils.VectorUtils;
import com.github.aadvorak.artilleryonline.battle.utils.VehicleUtils;

import java.util.stream.Collectors;

public class VehicleCollideProcessor {

    public static boolean processCollide(VehicleCalculations calculations, VehicleModel vehicleModel,
                                         BattleModel battleModel) {
        var collisionData = vehicleCollide(calculations, vehicleModel, battleModel);
        if (collisionData != null) {
            doCollide(vehicleModel, calculations, collisionData);
            vehicleModel.setUpdated(true);
            vehicleModel.setCollided(true);
            return true;
        }
        return false;
    }

    private static CollisionData vehicleCollide(VehicleCalculations calculations, VehicleModel vehicleModel,
                                               BattleModel battleModel) {
        var otherVehicleModels = battleModel.getVehicles().values().stream()
                .filter(value -> value.getId() != vehicleModel.getId())
                .collect(Collectors.toSet());
        var wheelRadius = vehicleModel.getSpecs().getWheelRadius();
        var vehicleRadius = vehicleModel.getSpecs().getRadius();
        var nextPosition = calculations.getNextPosition();
        var nextAngle = calculations.getNextAngle();
        var rightWheelPosition = VehicleUtils.getRightWheelPosition(vehicleModel, nextPosition, nextAngle);
        var leftWheelPosition = VehicleUtils.getLeftWheelPosition(vehicleModel, nextPosition, nextAngle);
        for (var otherVehicleModel : otherVehicleModels) {
            var otherVehiclePosition = otherVehicleModel.getState().getPosition();
            var otherWheelRadius = otherVehicleModel.getSpecs().getWheelRadius();
            var otherVehicleRadius = otherVehicleModel.getSpecs().getRadius();
            var minDistanceWheelWheel = wheelRadius + otherWheelRadius;
            var minDistanceVehicleVehicle = getMinVehicleVehicleDistance(nextPosition, otherVehiclePosition,
                    vehicleRadius, otherVehicleRadius);
            var minDistanceWheelVehicle = wheelRadius + otherVehicleRadius;
            var minDistanceVehicleWheel = vehicleRadius + otherWheelRadius;
            var distanceVehicleVehicle = nextPosition.distanceTo(otherVehiclePosition);
            if (distanceVehicleVehicle < minDistanceVehicleVehicle) {
                return new CollisionData(otherVehicleModel, null, null, null);
            }
            var otherLeftWheelPosition = VehicleUtils.getLeftWheelPosition(otherVehicleModel);
            var otherRightWheelPosition = VehicleUtils.getRightWheelPosition(otherVehicleModel);
            var otherLeftWheel = new WheelCalculations()
                    .setSign(WheelSign.LEFT)
                    .setPosition(otherLeftWheelPosition);
            var otherRightWheel = new WheelCalculations()
                    .setSign(WheelSign.RIGHT)
                    .setPosition(otherRightWheelPosition);
            var otherCalculations = new VehicleCalculations()
                    .setLeftWheel(otherLeftWheel)
                    .setRightWheel(otherRightWheel);
            var distanceRightWheelLeftWheel = rightWheelPosition.distanceTo(otherLeftWheelPosition);
            if (distanceRightWheelLeftWheel < minDistanceWheelWheel) {
                return new CollisionData(otherVehicleModel, otherCalculations, calculations.getRightWheel(), otherLeftWheel);
            }
            var distanceLeftWheelRightWheel = leftWheelPosition.distanceTo(otherRightWheelPosition);
            if (distanceLeftWheelRightWheel < minDistanceWheelWheel) {
                return new CollisionData(otherVehicleModel, otherCalculations, calculations.getLeftWheel(), otherRightWheel);
            }
            var distanceRightWheelVehicle = rightWheelPosition.distanceTo(otherVehiclePosition);
            if (distanceRightWheelVehicle < minDistanceWheelVehicle) {
                return new CollisionData(otherVehicleModel, otherCalculations, calculations.getRightWheel(), null);
            }
            var distanceVehicleRightWheel = nextPosition.distanceTo(otherRightWheelPosition);
            if (distanceVehicleRightWheel < minDistanceVehicleWheel) {
                return new CollisionData(otherVehicleModel, otherCalculations, null, otherRightWheel);
            }
            var distanceVehicleLeftWheel = nextPosition.distanceTo(otherLeftWheelPosition);
            if (distanceVehicleLeftWheel < minDistanceVehicleWheel) {
                return new CollisionData(otherVehicleModel, otherCalculations, null, otherLeftWheel);
            }
            var distanceLeftWheelVehicle = leftWheelPosition.distanceTo(otherVehiclePosition);
            if (distanceLeftWheelVehicle < minDistanceWheelVehicle) {
                return new CollisionData(otherVehicleModel, otherCalculations, calculations.getLeftWheel(), null);
            }
            var distanceLeftWheelLeftWheel = leftWheelPosition.distanceTo(otherLeftWheelPosition);
            if (distanceLeftWheelLeftWheel < minDistanceWheelWheel) {
                return new CollisionData(otherVehicleModel, otherCalculations, calculations.getLeftWheel(), otherLeftWheel);
            }
            var distanceRightWheelRightWheel = rightWheelPosition.distanceTo(otherRightWheelPosition);
            if (distanceRightWheelRightWheel < minDistanceWheelWheel) {
                return new CollisionData(otherVehicleModel, otherCalculations, calculations.getRightWheel(), otherRightWheel);
            }
        }
        return null;
    }

    private static double getMinVehicleVehicleDistance(Position position, Position otherPosition,
                                                       double radius, double otherRadius) {
        if (position.getY() - otherPosition.getY() > otherRadius) {
            return otherRadius;
        }
        if (otherPosition.getY() - position.getY() > radius) {
            return radius;
        }
        return radius + otherRadius;
    }

    private static void doCollide(VehicleModel vehicle, VehicleCalculations calculations, CollisionData collisionData) {
        if (collisionData.wheel() == null && collisionData.otherWheel() == null) {
            doCollideVehicleVehicle(vehicle, collisionData.otherVehicle());
        }
        if (collisionData.wheel() != null && collisionData.otherWheel() != null) {
            doCollideWheelWheel(vehicle, calculations, collisionData);
        }
        if (collisionData.wheel() != null && collisionData.otherWheel() == null) {
            doCollideWheelVehicle(vehicle, calculations, collisionData);
        }
        if (collisionData.wheel() == null && collisionData.otherWheel() != null) {
            doCollideVehicleWheel(vehicle, collisionData);
        }
        collisionData.otherVehicle().setCollided(true);
        collisionData.otherVehicle().setUpdated(true);
    }

    private static void doCollideVehicleVehicle(VehicleModel vehicle, VehicleModel otherVehicle) {
        var collisionAngle = getCollisionAngle(vehicle.getState().getPosition(), otherVehicle.getState().getPosition());

        var velocity = vehicle.getState().getVelocity().getMovingVelocity();
        var otherVelocity = otherVehicle.getState().getVelocity().getMovingVelocity();

        var velocityVerticalProjection = VectorUtils.getVerticalProjection(velocity, collisionAngle);
        var velocityHorizontalProjection = VectorUtils.getHorizontalProjection(velocity, collisionAngle);
        var otherVelocityVerticalProjection = VectorUtils.getVerticalProjection(otherVelocity, collisionAngle);

        var newVelocityVerticalProjection = getNewVelocityVerticalProjection(otherVehicle.isCollided(),
                velocityVerticalProjection, otherVelocityVerticalProjection, collisionAngle, vehicle.getState().getAngle());
        vehicle.getState().getVelocity()
                .setX(VectorUtils.getComponentX(newVelocityVerticalProjection, velocityHorizontalProjection, collisionAngle))
                .setY(VectorUtils.getComponentY(newVelocityVerticalProjection, velocityHorizontalProjection, collisionAngle));

        if (!otherVehicle.isCollided()) {
            var otherVelocityHorizontalProjection = VectorUtils.getHorizontalProjection(otherVelocity, collisionAngle);

            otherVehicle.getState().getVelocity()
                    .setX(VectorUtils.getComponentX(velocityVerticalProjection, otherVelocityHorizontalProjection, collisionAngle))
                    .setY(VectorUtils.getComponentY(velocityVerticalProjection, otherVelocityHorizontalProjection, collisionAngle));
        }
    }

    private static void doCollideWheelWheel(VehicleModel vehicle, VehicleCalculations calculations, CollisionData collisionData) {
        var collisionAngle = getCollisionAngle(collisionData.wheel().getPosition(), collisionData.otherWheel().getPosition());

        VehicleUtils.calculateWheelVelocity(vehicle, calculations.getRightWheel());
        VehicleUtils.calculateWheelVelocity(vehicle, calculations.getLeftWheel());
        VehicleUtils.calculateWheelVelocity(collisionData.otherVehicle(), collisionData.otherCalculations().getRightWheel());
        VehicleUtils.calculateWheelVelocity(collisionData.otherVehicle(), collisionData.otherCalculations().getLeftWheel());

        var velocityVerticalProjection = VectorUtils.getVerticalProjection(collisionData.wheel().getVelocity(), collisionAngle);
        var velocityHorizontalProjection = VectorUtils.getHorizontalProjection(collisionData.wheel().getVelocity(), collisionAngle);
        var otherVelocityVerticalProjection = VectorUtils.getVerticalProjection(collisionData.otherWheel().getVelocity(), collisionAngle);

        var newVelocityVerticalProjection = getNewVelocityVerticalProjection(collisionData.otherVehicle().isCollided(),
                velocityVerticalProjection, otherVelocityVerticalProjection, collisionAngle, vehicle.getState().getAngle());
        collisionData.wheel().getVelocity()
                .setX(VectorUtils.getComponentX(newVelocityVerticalProjection, velocityHorizontalProjection, collisionAngle))
                .setY(VectorUtils.getComponentY(newVelocityVerticalProjection, velocityHorizontalProjection, collisionAngle));

        VehicleUtils.recalculateVehicleVelocityByWheel(vehicle, calculations, collisionData.wheel());

        if (!collisionData.otherVehicle().isCollided()) {
            var otherVelocityHorizontalProjection = VectorUtils.getHorizontalProjection(collisionData.otherWheel().getVelocity(), collisionAngle);

            collisionData.otherWheel().getVelocity()
                    .setX(VectorUtils.getComponentX(velocityVerticalProjection, otherVelocityHorizontalProjection, collisionAngle))
                    .setY(VectorUtils.getComponentY(velocityVerticalProjection, otherVelocityHorizontalProjection, collisionAngle));

            VehicleUtils.recalculateVehicleVelocityByWheel(collisionData.otherVehicle(),
                    collisionData.otherCalculations(), collisionData.otherWheel());
        }
    }

    private static void doCollideWheelVehicle(VehicleModel vehicle, VehicleCalculations calculations, CollisionData collisionData) {
        var collisionAngle = getCollisionAngle(collisionData.wheel().getPosition(), collisionData.otherVehicle().getState().getPosition());
        var otherVelocity = collisionData.otherVehicle().getState().getVelocity().getMovingVelocity();

        VehicleUtils.calculateWheelVelocity(vehicle, calculations.getRightWheel());
        VehicleUtils.calculateWheelVelocity(vehicle, calculations.getLeftWheel());

        var velocityVerticalProjection = VectorUtils.getVerticalProjection(collisionData.wheel().getVelocity(), collisionAngle);
        var velocityHorizontalProjection = VectorUtils.getHorizontalProjection(collisionData.wheel().getVelocity(), collisionAngle);
        var otherVelocityVerticalProjection = VectorUtils.getVerticalProjection(otherVelocity, collisionAngle);

        var newVelocityVerticalProjection = getNewVelocityVerticalProjection(collisionData.otherVehicle().isCollided(),
                velocityVerticalProjection, otherVelocityVerticalProjection, collisionAngle, vehicle.getState().getAngle());
        collisionData.wheel().getVelocity()
                .setX(VectorUtils.getComponentX(newVelocityVerticalProjection, velocityHorizontalProjection, collisionAngle))
                .setY(VectorUtils.getComponentY(newVelocityVerticalProjection, velocityHorizontalProjection, collisionAngle));

        VehicleUtils.recalculateVehicleVelocityByWheel(vehicle, calculations, collisionData.wheel());

        if (!collisionData.otherVehicle().isCollided()) {
            var otherVelocityHorizontalProjection = VectorUtils.getHorizontalProjection(otherVelocity, collisionAngle);

            collisionData.otherVehicle().getState().getVelocity()
                    .setX(VectorUtils.getComponentX(velocityVerticalProjection, otherVelocityHorizontalProjection, collisionAngle))
                    .setY(VectorUtils.getComponentY(velocityVerticalProjection, otherVelocityHorizontalProjection, collisionAngle));
        }
    }

    private static void doCollideVehicleWheel(VehicleModel vehicle, CollisionData collisionData) {
        var collisionAngle = getCollisionAngle(vehicle.getState().getPosition(), collisionData.otherWheel().getPosition());
        var velocity = vehicle.getState().getVelocity().getMovingVelocity();

        VehicleUtils.calculateWheelVelocity(collisionData.otherVehicle(), collisionData.otherCalculations().getRightWheel());
        VehicleUtils.calculateWheelVelocity(collisionData.otherVehicle(), collisionData.otherCalculations().getLeftWheel());

        var velocityVerticalProjection = VectorUtils.getVerticalProjection(velocity, collisionAngle);
        var velocityHorizontalProjection = VectorUtils.getHorizontalProjection(velocity, collisionAngle);
        var otherVelocityVerticalProjection = VectorUtils.getVerticalProjection(collisionData.otherWheel().getVelocity(), collisionAngle);

        var newVelocityVerticalProjection = getNewVelocityVerticalProjection(collisionData.otherVehicle().isCollided(),
                velocityVerticalProjection, otherVelocityVerticalProjection, collisionAngle, vehicle.getState().getAngle());
        vehicle.getState().getVelocity()
                .setX(VectorUtils.getComponentX(newVelocityVerticalProjection, velocityHorizontalProjection, collisionAngle))
                .setY(VectorUtils.getComponentY(newVelocityVerticalProjection, velocityHorizontalProjection, collisionAngle));

        if (!collisionData.otherVehicle().isCollided()) {
            var otherVelocityHorizontalProjection = VectorUtils.getHorizontalProjection(collisionData.otherWheel().getVelocity(), collisionAngle);

            collisionData.otherWheel().getVelocity()
                    .setX(VectorUtils.getComponentX(velocityVerticalProjection, otherVelocityHorizontalProjection, collisionAngle))
                    .setY(VectorUtils.getComponentY(velocityVerticalProjection, otherVelocityHorizontalProjection, collisionAngle));

            VehicleUtils.recalculateVehicleVelocityByWheel(collisionData.otherVehicle(),
                    collisionData.otherCalculations(), collisionData.otherWheel());
        }
    }

    private static double getCollisionAngle(Position position, Position otherPosition) {
        return Math.asin((position.getY() - otherPosition.getY())
                / position.distanceTo(otherPosition)) - Math.PI / 2;
    }

    /**
     * todo this is temporal solution
     */
    private static double getNewVelocityVerticalProjection(
            boolean otherVehicleCollided, double velocityVerticalProjection,
            double otherVelocityVerticalProjection, double collisionAngle, double vehicleAngle
    ) {
        return otherVehicleCollided ? -velocityVerticalProjection
                : otherVelocityVerticalProjection - velocityVerticalProjection
                * Math.abs(Math.cos(vehicleAngle - collisionAngle));
    }

    private record CollisionData(VehicleModel otherVehicle, VehicleCalculations otherCalculations,
                                 WheelCalculations wheel, WheelCalculations otherWheel) {
    }
}
