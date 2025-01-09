package com.github.aadvorak.artilleryonline.battle.calculator.elasticity;

import com.github.aadvorak.artilleryonline.battle.calculations.BattleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.CollisionPair;
import com.github.aadvorak.artilleryonline.battle.calculations.VehicleCalculations;
import com.github.aadvorak.artilleryonline.battle.common.Position;
import com.github.aadvorak.artilleryonline.battle.common.VectorProjections;
import com.github.aadvorak.artilleryonline.battle.utils.InterpenetrationUtils;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class VehicleElasticityAccelerationCalculator {

    public static void calculate(VehicleCalculations vehicle, BattleCalculations battle) {
        var collisionPairs = getCollisionPairs(vehicle, battle);
        collisionPairs.forEach(VehicleElasticityAccelerationCalculator::calculateElasticityAcceleration);
    }

    private static Set<CollisionPair> getCollisionPairs(VehicleCalculations vehicle, BattleCalculations battle) {
        var collisionPairs = new HashSet<CollisionPair>();
        var otherVehicles = battle.getVehicles().stream()
                .filter(value -> value.getModel().getId() != vehicle.getModel().getId())
                .collect(Collectors.toSet());
        var wheelRadius = vehicle.getModel().getSpecs().getWheelRadius();
        var vehicleRadius = vehicle.getModel().getSpecs().getRadius();
        var position = vehicle.getModel().getState().getPosition();
        var angle = vehicle.getModel().getState().getAngle();
        var rightWheelPosition = vehicle.getRightWheel().getPosition();
        var leftWheelPosition = vehicle.getLeftWheel().getPosition();
        for (var otherVehicle : otherVehicles) {
            var otherVehiclePosition = otherVehicle.getModel().getState().getPosition();
            var otherVehicleAngle = otherVehicle.getModel().getState().getAngle();
            var otherWheelRadius = otherVehicle.getModel().getSpecs().getWheelRadius();
            var otherVehicleRadius = otherVehicle.getModel().getSpecs().getRadius();

            if (vehicle.collisionNotCalculated(otherVehicle) && InterpenetrationUtils.getVehicleVehicleInterpenetration(position,
                    otherVehiclePosition, angle, otherVehicleAngle, vehicleRadius, otherVehicleRadius) > 0) {
                collisionPairs.add(new CollisionPair(vehicle, otherVehicle));
            }
            var otherLeftWheelPosition = otherVehicle.getLeftWheel().getPosition();
            var otherRightWheelPosition = otherVehicle.getRightWheel().getPosition();
            var otherLeftWheel = otherVehicle.getLeftWheel();
            var otherRightWheel = otherVehicle.getRightWheel();
            var minDistanceWheelWheel = wheelRadius + otherWheelRadius;
            var distanceRightWheelLeftWheel = rightWheelPosition.distanceTo(otherLeftWheelPosition);
            if (vehicle.getRightWheel().collisionNotCalculated(otherLeftWheel)
                    && distanceRightWheelLeftWheel < minDistanceWheelWheel) {
                collisionPairs.add(new CollisionPair(vehicle.getRightWheel(), otherLeftWheel));
            }
            var distanceLeftWheelRightWheel = leftWheelPosition.distanceTo(otherRightWheelPosition);
            if (vehicle.getLeftWheel().collisionNotCalculated(otherRightWheel)
                    && distanceLeftWheelRightWheel < minDistanceWheelWheel) {
                collisionPairs.add(new CollisionPair(vehicle.getLeftWheel(), otherRightWheel));
            }
            if (vehicle.getRightWheel().collisionNotCalculated(otherVehicle)
                    && InterpenetrationUtils.getWheelVehicleInterpenetration(rightWheelPosition, otherVehiclePosition, otherVehicleAngle,
                    wheelRadius, otherVehicleRadius) > 0) {
                collisionPairs.add(new CollisionPair(vehicle.getRightWheel(), otherVehicle));
            }
            if (vehicle.collisionNotCalculated(otherRightWheel)
                    && InterpenetrationUtils.getWheelVehicleInterpenetration(otherRightWheelPosition, position, angle,
                    otherWheelRadius, vehicleRadius) > 0) {
                collisionPairs.add(new CollisionPair(vehicle, otherRightWheel));
            }
            if (vehicle.collisionNotCalculated(otherLeftWheel)
                    && InterpenetrationUtils.getWheelVehicleInterpenetration(otherLeftWheelPosition, position, angle,
                    otherWheelRadius, vehicleRadius) > 0) {
                collisionPairs.add(new CollisionPair(vehicle, otherLeftWheel));
            }
            if (vehicle.getLeftWheel().collisionNotCalculated(otherVehicle)
                    && InterpenetrationUtils.getWheelVehicleInterpenetration(leftWheelPosition, otherVehiclePosition, otherVehicleAngle,
                    wheelRadius, otherVehicleRadius) > 0) {
                collisionPairs.add(new CollisionPair(vehicle.getLeftWheel(), otherVehicle));
            }
            var distanceLeftWheelLeftWheel = leftWheelPosition.distanceTo(otherLeftWheelPosition);
            if (vehicle.getLeftWheel().collisionNotCalculated(otherLeftWheel)
                    && distanceLeftWheelLeftWheel < minDistanceWheelWheel) {
                collisionPairs.add(new CollisionPair(vehicle.getLeftWheel(), otherLeftWheel));
            }
            var distanceRightWheelRightWheel = rightWheelPosition.distanceTo(otherRightWheelPosition);
            if (vehicle.getRightWheel().collisionNotCalculated(otherRightWheel)
                    && distanceRightWheelRightWheel < minDistanceWheelWheel) {
                collisionPairs.add(new CollisionPair(vehicle.getRightWheel(), otherRightWheel));
            }
        }
        return collisionPairs;
    }

    private static void calculateElasticityAcceleration(CollisionPair collisionPair) {
        var coefficient = 100.0;
        var collisionAngle = getCollisionAngle(collisionPair.first().getPosition(), collisionPair.second().getPosition());

        // todo masses
        var firstAcceleration = new VectorProjections(collisionAngle).setNormal(coefficient).recoverAcceleration();
        var secondAcceleration = new VectorProjections(collisionAngle).setNormal(-coefficient).recoverAcceleration();

        collisionPair.first().getVehicleElasticityAcceleration().add(firstAcceleration);
        collisionPair.second().getVehicleElasticityAcceleration().add(secondAcceleration);
        collisionPair.first().addVehicleCollision(collisionPair.second());
        collisionPair.second().addVehicleCollision(collisionPair.first());
    }

    private static double getCollisionAngle(Position position, Position otherPosition) {
        return Math.asin((position.getY() - otherPosition.getY())
                / position.distanceTo(otherPosition)) - Math.PI / 2;
    }
}
