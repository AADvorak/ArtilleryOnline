package com.github.aadvorak.artilleryonline.battle.processor.vehicle.collisions;

import com.github.aadvorak.artilleryonline.battle.calculations.BattleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.VehicleCalculations;
import com.github.aadvorak.artilleryonline.battle.common.CollideObjectType;
import com.github.aadvorak.artilleryonline.battle.common.Collision;
import com.github.aadvorak.artilleryonline.battle.utils.InterpenetrationUtils;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class VehicleCollisionsDetector {

    public static Collision detectFirst(VehicleCalculations vehicle, BattleCalculations battle) {
        var collisions = detect(vehicle, battle, true);
        if (collisions.isEmpty()) {
            return null;
        }
        return collisions.iterator().next();
    }

    public static Set<Collision> detect(VehicleCalculations vehicle, BattleCalculations battle, boolean first) {
        Set<Collision> collisions = new HashSet<>();
        var otherVehicles = battle.getVehicles().stream()
                .filter(value -> !Objects.equals(value.getVehicleId(), vehicle.getVehicleId()))
                .filter(value -> collisionNotDetected(vehicle, value.getVehicleId()))
                .collect(Collectors.toSet());
        var wheelRadius = vehicle.getModel().getSpecs().getWheelRadius();
        var vehicleRadius = vehicle.getModel().getSpecs().getRadius();
        var position = vehicle.getNextPosition();
        var angle = vehicle.getNextAngle();
        var rightWheelPosition = vehicle.getRightWheel().getNext().getPosition();
        var leftWheelPosition = vehicle.getLeftWheel().getNext().getPosition();
        for (var otherVehicle : otherVehicles) {
            var otherPosition = otherVehicle.getNextPosition();
            var otherAngle = otherVehicle.getNextAngle();
            var otherVehicleRadius = otherVehicle.getModel().getSpecs().getRadius();
            var vehicleVehicleInterpenetration = InterpenetrationUtils.getVehicleVehicleInterpenetration(position,
                    otherPosition, angle, otherAngle, vehicleRadius, otherVehicleRadius);
            if (vehicleVehicleInterpenetration > 0) {
                collisions.add(Collision.ofVehicles(vehicle, otherVehicle, vehicleVehicleInterpenetration));
                if (first) return collisions;
            }
            var otherWheelRadius = otherVehicle.getModel().getSpecs().getWheelRadius();
            var otherLeftWheelPosition = otherVehicle.getLeftWheel().getNext().getPosition();
            var otherRightWheelPosition = otherVehicle.getRightWheel().getNext().getPosition();
            var otherLeftWheel = otherVehicle.getLeftWheel();
            var otherRightWheel = otherVehicle.getRightWheel();
            var rightWheelLeftWheelInterpenetration = InterpenetrationUtils.getWheelWheelInterpenetration(rightWheelPosition,
                    otherLeftWheelPosition, wheelRadius, otherWheelRadius);
            if (rightWheelLeftWheelInterpenetration > 0) {
                collisions.add(Collision.ofVehicles(vehicle.getRightWheel(), otherLeftWheel, rightWheelLeftWheelInterpenetration));
                if (first) return collisions;
            }
            var leftWheelRightWheelInterpenetration = InterpenetrationUtils.getWheelWheelInterpenetration(leftWheelPosition,
                    otherRightWheelPosition, wheelRadius, otherWheelRadius);
            if (leftWheelRightWheelInterpenetration > 0) {
                collisions.add(Collision.ofVehicles(vehicle.getLeftWheel(), otherRightWheel, leftWheelRightWheelInterpenetration));
                if (first) return collisions;
            }
            var rightWheelVehicleInterpenetration = InterpenetrationUtils.getWheelVehicleInterpenetration(rightWheelPosition,
                    otherPosition, otherAngle, wheelRadius, otherVehicleRadius);
            if (rightWheelVehicleInterpenetration > 0) {
                collisions.add(Collision.ofVehicles(vehicle.getRightWheel(), otherVehicle, rightWheelVehicleInterpenetration));
                if (first) return collisions;
            }
            var vehicleRightWheelInterpenetration = InterpenetrationUtils.getWheelVehicleInterpenetration(
                    otherRightWheelPosition, position, angle, otherWheelRadius, vehicleRadius);
            if (vehicleRightWheelInterpenetration > 0) {
                collisions.add(Collision.ofVehicles(vehicle, otherRightWheel, vehicleRightWheelInterpenetration));
                if (first) return collisions;
            }
            var vehicleLeftWheelInterpenetration = InterpenetrationUtils.getWheelVehicleInterpenetration(otherLeftWheelPosition,
                    position, angle, otherWheelRadius, vehicleRadius);
            if (vehicleLeftWheelInterpenetration > 0) {
                collisions.add(Collision.ofVehicles(vehicle, otherLeftWheel, vehicleLeftWheelInterpenetration));
                if (first) return collisions;
            }
            var leftWheelVehicleInterpenetration = InterpenetrationUtils.getWheelVehicleInterpenetration(leftWheelPosition,
                    otherPosition, otherAngle, wheelRadius, otherVehicleRadius);
            if (leftWheelVehicleInterpenetration > 0) {
                collisions.add(Collision.ofVehicles(vehicle.getLeftWheel(), otherVehicle, leftWheelVehicleInterpenetration));
                if (first) return collisions;
            }
            var leftWheelLeftWheelInterpenetration = InterpenetrationUtils.getWheelWheelInterpenetration(leftWheelPosition,
                    otherLeftWheelPosition, wheelRadius, otherWheelRadius);
            if (leftWheelLeftWheelInterpenetration > 0) {
                collisions.add(Collision.ofVehicles(vehicle.getLeftWheel(), otherLeftWheel, leftWheelLeftWheelInterpenetration));
                if (first) return collisions;
            }
            var rightWheelRightWheelInterpenetration = InterpenetrationUtils.getWheelWheelInterpenetration(rightWheelPosition,
                    otherRightWheelPosition, wheelRadius, otherWheelRadius);
            if (rightWheelRightWheelInterpenetration > 0) {
                collisions.add(Collision.ofVehicles(vehicle.getRightWheel(), otherRightWheel, rightWheelRightWheelInterpenetration));
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
