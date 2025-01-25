package com.github.aadvorak.artilleryonline.battle.processor.vehicle.collisions;

import com.github.aadvorak.artilleryonline.battle.calculations.BattleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.VehicleCalculations;
import com.github.aadvorak.artilleryonline.battle.common.CollideObjectType;
import com.github.aadvorak.artilleryonline.battle.common.Collision;
import com.github.aadvorak.artilleryonline.battle.common.Constants;
import com.github.aadvorak.artilleryonline.battle.common.lines.Circle;
import com.github.aadvorak.artilleryonline.battle.common.lines.HalfCircle;
import com.github.aadvorak.artilleryonline.battle.utils.InterpenetrationUtils;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class VehicleVehicleCollisionsDetector {

    public static Collision detectFirst(VehicleCalculations vehicle, BattleCalculations battle) {
        var collisions = detect(vehicle, battle, true);
        if (collisions.isEmpty()) {
            return null;
        }
        return collisions.iterator().next();
    }

    public static Collision detectStrongest(VehicleCalculations vehicle, BattleCalculations battle) {
        var collisions = detect(vehicle, battle, false);
        if (collisions.isEmpty()) {
            return null;
        }
        var iterator = collisions.iterator();
        var strongest = iterator.next();
        while (iterator.hasNext()) {
            var collision = iterator.next();
            var collisionVelocity = collision.getSumNormalVelocity();
            var strongestVelocity = strongest.getSumNormalVelocity();
            if (collisionVelocity < 1.0 && strongestVelocity < 1.0
                    && collision.getInterpenetration() > strongest.getInterpenetration()) {
                strongest = collision;
            } else if (collisionVelocity > strongestVelocity) {
                strongest = collision;
            }
        }
        return strongest;
    }

    // todo refactor
    public static Set<Collision> detect(VehicleCalculations vehicle, BattleCalculations battle, boolean first) {
        Set<Collision> collisions = new HashSet<>();
        var otherVehicles = battle.getVehicles().stream()
                .filter(value -> !Objects.equals(value.getId(), vehicle.getId()))
                .filter(value -> collisionNotDetected(vehicle, value.getId()))
                .collect(Collectors.toSet());
        var wheelRadius = vehicle.getModel().getSpecs().getWheelRadius();
        var vehicleRadius = vehicle.getModel().getSpecs().getRadius();
        var position = vehicle.getNextPosition();
        var angle = vehicle.getNextAngle();
        var rightWheelPosition = vehicle.getRightWheel().getNext().getPosition();
        var leftWheelPosition = vehicle.getLeftWheel().getNext().getPosition();
        var vehicleShape = new HalfCircle(position, vehicleRadius, angle);
        var rightWheelShape = new Circle(rightWheelPosition, wheelRadius);
        var leftWheelShape = new Circle(leftWheelPosition, wheelRadius);
        for (var otherVehicle : otherVehicles) {
            var otherPosition = otherVehicle.getNextPosition();
            var otherAngle = otherVehicle.getNextAngle();
            var otherVehicleRadius = otherVehicle.getModel().getSpecs().getRadius();
            var otherVehicleShape = new HalfCircle(otherPosition, otherVehicleRadius, otherAngle);
            var vehicleVehicleInterpenetration = InterpenetrationUtils.getVehicleVehicleInterpenetration(vehicleShape, otherVehicleShape);
            if (vehicleVehicleInterpenetration > Constants.INTERPENETRATION_THRESHOLD) {
                collisions.add(Collision.ofTwoVehicles(vehicle, otherVehicle, vehicleVehicleInterpenetration));
                if (first) return collisions;
            }
            var otherWheelRadius = otherVehicle.getModel().getSpecs().getWheelRadius();
            var otherLeftWheelPosition = otherVehicle.getLeftWheel().getNext().getPosition();
            var otherRightWheelPosition = otherVehicle.getRightWheel().getNext().getPosition();
            var otherLeftWheel = otherVehicle.getLeftWheel();
            var otherRightWheel = otherVehicle.getRightWheel();
            var otherLeftWheelShape = new Circle(otherLeftWheelPosition, otherWheelRadius);
            var otherRightWheelShape = new Circle(otherRightWheelPosition, otherWheelRadius);
            var rightWheelLeftWheelInterpenetration = InterpenetrationUtils.getWheelWheelInterpenetration(rightWheelShape, otherLeftWheelShape);
            if (rightWheelLeftWheelInterpenetration > Constants.INTERPENETRATION_THRESHOLD) {
                collisions.add(Collision.ofTwoVehicles(vehicle.getRightWheel(), otherLeftWheel, rightWheelLeftWheelInterpenetration));
                if (first) return collisions;
            }
            var leftWheelRightWheelInterpenetration = InterpenetrationUtils.getWheelWheelInterpenetration(leftWheelShape, otherRightWheelShape);
            if (leftWheelRightWheelInterpenetration > Constants.INTERPENETRATION_THRESHOLD) {
                collisions.add(Collision.ofTwoVehicles(vehicle.getLeftWheel(), otherRightWheel, leftWheelRightWheelInterpenetration));
                if (first) return collisions;
            }
            var rightWheelVehicleInterpenetration = InterpenetrationUtils.getWheelVehicleInterpenetration(rightWheelShape, otherVehicleShape);
            if (rightWheelVehicleInterpenetration > Constants.INTERPENETRATION_THRESHOLD) {
                collisions.add(Collision.ofTwoVehicles(vehicle.getRightWheel(), otherVehicle, rightWheelVehicleInterpenetration));
                if (first) return collisions;
            }
            var vehicleRightWheelInterpenetration = InterpenetrationUtils.getWheelVehicleInterpenetration(otherRightWheelShape, vehicleShape);
            if (vehicleRightWheelInterpenetration > Constants.INTERPENETRATION_THRESHOLD) {
                collisions.add(Collision.ofTwoVehicles(vehicle, otherRightWheel, vehicleRightWheelInterpenetration));
                if (first) return collisions;
            }
            var vehicleLeftWheelInterpenetration = InterpenetrationUtils.getWheelVehicleInterpenetration(otherLeftWheelShape, vehicleShape);
            if (vehicleLeftWheelInterpenetration > Constants.INTERPENETRATION_THRESHOLD) {
                collisions.add(Collision.ofTwoVehicles(vehicle, otherLeftWheel, vehicleLeftWheelInterpenetration));
                if (first) return collisions;
            }
            var leftWheelVehicleInterpenetration = InterpenetrationUtils.getWheelVehicleInterpenetration(leftWheelShape, otherVehicleShape);
            if (leftWheelVehicleInterpenetration > Constants.INTERPENETRATION_THRESHOLD) {
                collisions.add(Collision.ofTwoVehicles(vehicle.getLeftWheel(), otherVehicle, leftWheelVehicleInterpenetration));
                if (first) return collisions;
            }
            var leftWheelLeftWheelInterpenetration = InterpenetrationUtils.getWheelWheelInterpenetration(leftWheelShape, otherLeftWheelShape);
            if (leftWheelLeftWheelInterpenetration > Constants.INTERPENETRATION_THRESHOLD) {
                collisions.add(Collision.ofTwoVehicles(vehicle.getLeftWheel(), otherLeftWheel, leftWheelLeftWheelInterpenetration));
                if (first) return collisions;
            }
            var rightWheelRightWheelInterpenetration = InterpenetrationUtils.getWheelWheelInterpenetration(rightWheelShape, otherRightWheelShape);
            if (rightWheelRightWheelInterpenetration > Constants.INTERPENETRATION_THRESHOLD) {
                collisions.add(Collision.ofTwoVehicles(vehicle.getRightWheel(), otherRightWheel, rightWheelRightWheelInterpenetration));
                if (first) return collisions;
            }
        }
        return collisions;
    }

    // todo vehicle collisions checked
    private static boolean collisionNotDetected(VehicleCalculations vehicle, Integer otherVehicleId) {
        return vehicle.getCollisions().stream()
                .noneMatch(c -> CollideObjectType.VEHICLE.equals(c.getType())
                        && c.getVehicleId().equals(otherVehicleId));
    }
}
