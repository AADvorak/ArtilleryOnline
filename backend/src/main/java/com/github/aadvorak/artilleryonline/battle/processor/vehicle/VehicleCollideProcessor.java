package com.github.aadvorak.artilleryonline.battle.processor.vehicle;

import com.github.aadvorak.artilleryonline.battle.calculations.BattleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.Collision;
import com.github.aadvorak.artilleryonline.battle.calculations.VehicleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.WheelCalculations;
import com.github.aadvorak.artilleryonline.battle.common.Position;
import com.github.aadvorak.artilleryonline.battle.model.VehicleModel;
import com.github.aadvorak.artilleryonline.battle.utils.CollideUtils;
import com.github.aadvorak.artilleryonline.battle.utils.VectorUtils;
import com.github.aadvorak.artilleryonline.battle.utils.VehicleUtils;

import java.util.stream.Collectors;

public class VehicleCollideProcessor {

    public static boolean processCollide(VehicleCalculations vehicle, BattleCalculations battle) {
        var collisionData = vehicleCollide(vehicle, battle);
        if (collisionData != null) {
            doCollide(vehicle, collisionData);
            vehicle.getModel().setUpdated(true);
            vehicle.getCollisions().add(Collision.VEHICLE);
            return true;
        }
        return false;
    }

    private static CollisionData vehicleCollide(VehicleCalculations vehicle, BattleCalculations battle) {
        var otherVehicles = battle.getVehicles().stream()
                .filter(value -> value.getModel().getId() != vehicle.getModel().getId())
                .collect(Collectors.toSet());
        var wheelRadius = vehicle.getModel().getSpecs().getWheelRadius();
        var vehicleRadius = vehicle.getModel().getSpecs().getRadius();
        var nextPosition = vehicle.getNextPosition();
        var nextAngle = vehicle.getNextAngle();
        var rightWheelPosition = VehicleUtils.getNextRightWheelPosition(vehicle);
        var leftWheelPosition = VehicleUtils.getNextLeftWheelPosition(vehicle);
        for (var otherVehicle : otherVehicles) {
            var otherVehiclePosition = otherVehicle.getNextPosition();
            var otherVehicleAngle = otherVehicle.getNextAngle();
            var otherWheelRadius = otherVehicle.getModel().getSpecs().getWheelRadius();
            var otherVehicleRadius = otherVehicle.getModel().getSpecs().getRadius();
            var minDistanceWheelWheel = wheelRadius + otherWheelRadius;
            if (CollideUtils.isVehicleVehicleCollide(nextPosition, otherVehiclePosition, nextAngle,
                    otherVehicleAngle, vehicleRadius, otherVehicleRadius)) {
                return new CollisionData(otherVehicle, null, null);
            }
            var otherLeftWheelPosition = VehicleUtils.getNextLeftWheelPosition(otherVehicle);
            var otherRightWheelPosition = VehicleUtils.getNextRightWheelPosition(otherVehicle);
            var otherLeftWheel = otherVehicle.getLeftWheel();
            var otherRightWheel = otherVehicle.getRightWheel();
            var distanceRightWheelLeftWheel = rightWheelPosition.distanceTo(otherLeftWheelPosition);
            if (distanceRightWheelLeftWheel < minDistanceWheelWheel) {
                return new CollisionData(otherVehicle, vehicle.getRightWheel(), otherLeftWheel);
            }
            var distanceLeftWheelRightWheel = leftWheelPosition.distanceTo(otherRightWheelPosition);
            if (distanceLeftWheelRightWheel < minDistanceWheelWheel) {
                return new CollisionData(otherVehicle, vehicle.getLeftWheel(), otherRightWheel);
            }
            if (CollideUtils.isWheelVehicleCollide(rightWheelPosition, otherVehiclePosition, otherVehicleAngle,
                    wheelRadius, otherVehicleRadius)) {
                return new CollisionData(otherVehicle, vehicle.getRightWheel(), null);
            }
            if (CollideUtils.isWheelVehicleCollide(otherRightWheelPosition, nextPosition, nextAngle,
                    otherWheelRadius, vehicleRadius)) {
                return new CollisionData(otherVehicle, null, otherRightWheel);
            }
            if (CollideUtils.isWheelVehicleCollide(otherLeftWheelPosition, nextPosition, nextAngle,
                    otherWheelRadius, vehicleRadius)) {
                return new CollisionData(otherVehicle, null, otherLeftWheel);
            }
            if (CollideUtils.isWheelVehicleCollide(leftWheelPosition, otherVehiclePosition, otherVehicleAngle,
                    wheelRadius, otherVehicleRadius)) {
                return new CollisionData(otherVehicle, vehicle.getLeftWheel(), null);
            }
            var distanceLeftWheelLeftWheel = leftWheelPosition.distanceTo(otherLeftWheelPosition);
            if (distanceLeftWheelLeftWheel < minDistanceWheelWheel) {
                return new CollisionData(otherVehicle, vehicle.getLeftWheel(), otherLeftWheel);
            }
            var distanceRightWheelRightWheel = rightWheelPosition.distanceTo(otherRightWheelPosition);
            if (distanceRightWheelRightWheel < minDistanceWheelWheel) {
                return new CollisionData(otherVehicle, vehicle.getRightWheel(), otherRightWheel);
            }
        }
        return null;
    }

    private static void doCollide(VehicleCalculations vehicle, CollisionData collisionData) {
        if (collisionData.wheel() == null && collisionData.otherWheel() == null) {
            doCollideVehicleVehicle(vehicle.getModel(), collisionData.otherVehicle().getModel());
        }
        if (collisionData.wheel() != null && collisionData.otherWheel() != null) {
            doCollideWheelWheel(vehicle, collisionData);
        }
        if (collisionData.wheel() != null && collisionData.otherWheel() == null) {
            doCollideWheelVehicle(vehicle, collisionData);
        }
        if (collisionData.wheel() == null && collisionData.otherWheel() != null) {
            doCollideVehicleWheel(vehicle.getModel(), collisionData);
        }
        collisionData.otherVehicle().getCollisions().add(Collision.VEHICLE);
        collisionData.otherVehicle().getModel().setUpdated(true);
    }

    private static void doCollideVehicleVehicle(VehicleModel vehicle, VehicleModel otherVehicle) {
        var collisionAngle = getCollisionAngle(vehicle.getState().getPosition(), otherVehicle.getState().getPosition());

        var velocity = vehicle.getState().getVelocity().getMovingVelocity();
        var otherVelocity = otherVehicle.getState().getVelocity().getMovingVelocity();

        var velocityVerticalProjection = VectorUtils.getVerticalProjection(velocity, collisionAngle);
        var velocityHorizontalProjection = VectorUtils.getHorizontalProjection(velocity, collisionAngle);
        var otherVelocityVerticalProjection = VectorUtils.getVerticalProjection(otherVelocity, collisionAngle);

        var newVelocityVerticalProjection = getNewVelocityVerticalProjection(velocityVerticalProjection,
                otherVelocityVerticalProjection, collisionAngle, vehicle.getState().getAngle());
        vehicle.getState().getVelocity()
                .setX(VectorUtils.getComponentX(newVelocityVerticalProjection, velocityHorizontalProjection, collisionAngle))
                .setY(VectorUtils.getComponentY(newVelocityVerticalProjection, velocityHorizontalProjection, collisionAngle));

        var otherVelocityHorizontalProjection = VectorUtils.getHorizontalProjection(otherVelocity, collisionAngle);
        var otherNewVelocityVerticalProjection = getNewVelocityVerticalProjection(otherVelocityVerticalProjection,
                velocityVerticalProjection, collisionAngle, otherVehicle.getState().getAngle());
        otherVehicle.getState().getVelocity()
                .setX(VectorUtils.getComponentX(otherNewVelocityVerticalProjection, otherVelocityHorizontalProjection, collisionAngle))
                .setY(VectorUtils.getComponentY(otherNewVelocityVerticalProjection, otherVelocityHorizontalProjection, collisionAngle));
    }

    private static void doCollideWheelWheel(VehicleCalculations vehicle, CollisionData collisionData) {
        var collisionAngle = getCollisionAngle(collisionData.wheel().getPosition(), collisionData.otherWheel().getPosition());

        VehicleUtils.calculateWheelVelocity(vehicle.getModel(), vehicle.getRightWheel());
        VehicleUtils.calculateWheelVelocity(vehicle.getModel(), vehicle.getLeftWheel());
        VehicleUtils.calculateWheelVelocity(collisionData.otherVehicle().getModel(), collisionData.otherVehicle().getRightWheel());
        VehicleUtils.calculateWheelVelocity(collisionData.otherVehicle().getModel(), collisionData.otherVehicle().getLeftWheel());

        var velocityVerticalProjection = VectorUtils.getVerticalProjection(collisionData.wheel().getVelocity(), collisionAngle);
        var velocityHorizontalProjection = VectorUtils.getHorizontalProjection(collisionData.wheel().getVelocity(), collisionAngle);
        var otherVelocityVerticalProjection = VectorUtils.getVerticalProjection(collisionData.otherWheel().getVelocity(), collisionAngle);

        var newVelocityVerticalProjection = getNewVelocityVerticalProjection(velocityVerticalProjection,
                otherVelocityVerticalProjection, collisionAngle, vehicle.getModel().getState().getAngle());
        collisionData.wheel().getVelocity()
                .setX(VectorUtils.getComponentX(newVelocityVerticalProjection, velocityHorizontalProjection, collisionAngle))
                .setY(VectorUtils.getComponentY(newVelocityVerticalProjection, velocityHorizontalProjection, collisionAngle));

        VehicleUtils.recalculateVehicleVelocityByWheel(vehicle, collisionData.wheel());

        var otherVelocityHorizontalProjection = VectorUtils.getHorizontalProjection(collisionData.otherWheel().getVelocity(), collisionAngle);
        var otherNewVelocityVerticalProjection = getNewVelocityVerticalProjection(otherVelocityVerticalProjection,
                velocityVerticalProjection, collisionAngle, collisionData.otherVehicle().getModel().getState().getAngle());
        collisionData.otherWheel().getVelocity()
                .setX(VectorUtils.getComponentX(otherNewVelocityVerticalProjection, otherVelocityHorizontalProjection, collisionAngle))
                .setY(VectorUtils.getComponentY(otherNewVelocityVerticalProjection, otherVelocityHorizontalProjection, collisionAngle));

        VehicleUtils.recalculateVehicleVelocityByWheel(collisionData.otherVehicle(), collisionData.otherWheel());
    }

    private static void doCollideWheelVehicle(VehicleCalculations vehicle, CollisionData collisionData) {
        var collisionAngle = getCollisionAngle(collisionData.wheel().getPosition(),
                collisionData.otherVehicle().getModel().getState().getPosition());
        var otherVelocity = collisionData.otherVehicle().getModel().getState().getVelocity().getMovingVelocity();

        VehicleUtils.calculateWheelVelocity(vehicle.getModel(), vehicle.getRightWheel());
        VehicleUtils.calculateWheelVelocity(vehicle.getModel(), vehicle.getLeftWheel());

        var velocityVerticalProjection = VectorUtils.getVerticalProjection(collisionData.wheel().getVelocity(), collisionAngle);
        var velocityHorizontalProjection = VectorUtils.getHorizontalProjection(collisionData.wheel().getVelocity(), collisionAngle);
        var otherVelocityVerticalProjection = VectorUtils.getVerticalProjection(otherVelocity, collisionAngle);

        var newVelocityVerticalProjection = getNewVelocityVerticalProjection(velocityVerticalProjection,
                otherVelocityVerticalProjection, collisionAngle, vehicle.getModel().getState().getAngle());
        collisionData.wheel().getVelocity()
                .setX(VectorUtils.getComponentX(newVelocityVerticalProjection, velocityHorizontalProjection, collisionAngle))
                .setY(VectorUtils.getComponentY(newVelocityVerticalProjection, velocityHorizontalProjection, collisionAngle));

        VehicleUtils.recalculateVehicleVelocityByWheel(vehicle, collisionData.wheel());

        var otherVelocityHorizontalProjection = VectorUtils.getHorizontalProjection(otherVelocity, collisionAngle);
        var otherNewVelocityVerticalProjection = getNewVelocityVerticalProjection(otherVelocityVerticalProjection,
                velocityVerticalProjection, collisionAngle, collisionData.otherVehicle().getModel().getState().getAngle());
        collisionData.otherVehicle().getModel().getState().getVelocity()
                .setX(VectorUtils.getComponentX(otherNewVelocityVerticalProjection, otherVelocityHorizontalProjection, collisionAngle))
                .setY(VectorUtils.getComponentY(otherNewVelocityVerticalProjection, otherVelocityHorizontalProjection, collisionAngle));
    }

    private static void doCollideVehicleWheel(VehicleModel vehicle, CollisionData collisionData) {
        var collisionAngle = getCollisionAngle(vehicle.getState().getPosition(), collisionData.otherWheel().getPosition());
        var velocity = vehicle.getState().getVelocity().getMovingVelocity();

        VehicleUtils.calculateWheelVelocity(collisionData.otherVehicle().getModel(), collisionData.otherVehicle().getRightWheel());
        VehicleUtils.calculateWheelVelocity(collisionData.otherVehicle().getModel(), collisionData.otherVehicle().getLeftWheel());

        var velocityVerticalProjection = VectorUtils.getVerticalProjection(velocity, collisionAngle);
        var velocityHorizontalProjection = VectorUtils.getHorizontalProjection(velocity, collisionAngle);
        var otherVelocityVerticalProjection = VectorUtils.getVerticalProjection(collisionData.otherWheel().getVelocity(), collisionAngle);

        var newVelocityVerticalProjection = getNewVelocityVerticalProjection(velocityVerticalProjection,
                otherVelocityVerticalProjection, collisionAngle, vehicle.getState().getAngle());
        vehicle.getState().getVelocity()
                .setX(VectorUtils.getComponentX(newVelocityVerticalProjection, velocityHorizontalProjection, collisionAngle))
                .setY(VectorUtils.getComponentY(newVelocityVerticalProjection, velocityHorizontalProjection, collisionAngle));

        var otherVelocityHorizontalProjection = VectorUtils.getHorizontalProjection(collisionData.otherWheel().getVelocity(), collisionAngle);
        var otherNewVelocityVerticalProjection = getNewVelocityVerticalProjection(otherVelocityVerticalProjection,
                velocityVerticalProjection, collisionAngle, collisionData.otherVehicle().getModel().getState().getAngle());
        collisionData.otherWheel().getVelocity()
                .setX(VectorUtils.getComponentX(otherNewVelocityVerticalProjection, otherVelocityHorizontalProjection, collisionAngle))
                .setY(VectorUtils.getComponentY(otherNewVelocityVerticalProjection, otherVelocityHorizontalProjection, collisionAngle));

        VehicleUtils.recalculateVehicleVelocityByWheel(collisionData.otherVehicle(), collisionData.otherWheel());
    }

    private static double getCollisionAngle(Position position, Position otherPosition) {
        return Math.asin((position.getY() - otherPosition.getY())
                / position.distanceTo(otherPosition)) - Math.PI / 2;
    }

    /**
     * todo this is temporal solution
     */
    private static double getNewVelocityVerticalProjection(
            double velocityVerticalProjection, double otherVelocityVerticalProjection,
            double collisionAngle, double vehicleAngle
    ) {
        return otherVelocityVerticalProjection - velocityVerticalProjection
                * Math.abs(Math.cos(vehicleAngle - collisionAngle));
    }

    private record CollisionData(
            VehicleCalculations otherVehicle,
            WheelCalculations wheel,
            WheelCalculations otherWheel
    ) {
    }
}
