package com.github.aadvorak.artilleryonline.battle.collision.detector.vehicle;

import com.github.aadvorak.artilleryonline.battle.calculations.BattleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.Calculations;
import com.github.aadvorak.artilleryonline.battle.calculations.VehicleCalculations;
import com.github.aadvorak.artilleryonline.battle.collision.detector.CollisionsDetector;
import com.github.aadvorak.artilleryonline.battle.common.CollideObjectType;
import com.github.aadvorak.artilleryonline.battle.common.Collision;
import com.github.aadvorak.artilleryonline.battle.common.lines.Circle;
import com.github.aadvorak.artilleryonline.battle.common.lines.HalfCircle;
import com.github.aadvorak.artilleryonline.battle.utils.ContactUtils;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class VehicleVehicleCollisionsDetector implements CollisionsDetector {

    @Override
    public Set<Collision> detect(Calculations<?> calculations, BattleCalculations battle, boolean first) {
        if (calculations instanceof VehicleCalculations vehicleCalculations) {
            return detect(vehicleCalculations, battle, first);
        }
        return Set.of();
    }

    // todo refactor
    private Set<Collision> detect(VehicleCalculations vehicle, BattleCalculations battle, boolean first) {
        Set<Collision> collisions = new HashSet<>();
        var otherVehicles = battle.getVehicles().stream()
                .filter(value -> !Objects.equals(value.getId(), vehicle.getId()))
                .filter(value -> collisionNotDetected(vehicle, value))
                .collect(Collectors.toSet());
        var wheelRadius = vehicle.getModel().getSpecs().getWheelRadius();
        var vehicleRadius = vehicle.getModel().getSpecs().getRadius();
        var position = vehicle.getGeometryNextPosition();
        var rightWheelPosition = vehicle.getRightWheel().getNext().getPosition();
        var leftWheelPosition = vehicle.getLeftWheel().getNext().getPosition();
        var vehicleShape = HalfCircle.of(position, vehicleRadius);
        var rightWheelShape = new Circle(rightWheelPosition, wheelRadius);
        var leftWheelShape = new Circle(leftWheelPosition, wheelRadius);
        for (var otherVehicle : otherVehicles) {
            var otherPosition = otherVehicle.getGeometryNextPosition();
            var otherVehicleRadius = otherVehicle.getModel().getSpecs().getRadius();
            var otherVehicleShape = HalfCircle.of(otherPosition, otherVehicleRadius);
            var vehicleVehicleContact = ContactUtils.getHalfCirclesContact(vehicleShape, otherVehicleShape);
            if (vehicleVehicleContact != null) {
                collisions.add(Collision.withVehicle(vehicle, otherVehicle, vehicleVehicleContact));
                if (first) return collisions;
            }
            var otherWheelRadius = otherVehicle.getModel().getSpecs().getWheelRadius();
            var otherLeftWheelPosition = otherVehicle.getLeftWheel().getNext().getPosition();
            var otherRightWheelPosition = otherVehicle.getRightWheel().getNext().getPosition();
            var otherLeftWheel = otherVehicle.getLeftWheel();
            var otherRightWheel = otherVehicle.getRightWheel();
            var otherLeftWheelShape = new Circle(otherLeftWheelPosition, otherWheelRadius);
            var otherRightWheelShape = new Circle(otherRightWheelPosition, otherWheelRadius);
            var rightWheelLeftWheelContact = ContactUtils.getCirclesContact(rightWheelShape, otherLeftWheelShape);
            if (rightWheelLeftWheelContact != null) {
                collisions.add(Collision.withVehicle(vehicle.getRightWheel(), otherLeftWheel, rightWheelLeftWheelContact));
                if (first) return collisions;
            }
            var leftWheelRightWheelContact = ContactUtils.getCirclesContact(leftWheelShape, otherRightWheelShape);
            if (leftWheelRightWheelContact != null) {
                collisions.add(Collision.withVehicle(vehicle.getLeftWheel(), otherRightWheel, leftWheelRightWheelContact));
                if (first) return collisions;
            }
            var rightWheelVehicleContact = ContactUtils.getCircleHalfCircleContact(rightWheelShape, otherVehicleShape);
            if (rightWheelVehicleContact != null) {
                collisions.add(Collision.withVehicle(vehicle.getRightWheel(), otherVehicle, rightWheelVehicleContact));
                if (first) return collisions;
            }
            var vehicleRightWheelContact = ContactUtils.getCircleHalfCircleContact(otherRightWheelShape, vehicleShape);
            if (vehicleRightWheelContact != null) {
                collisions.add(Collision.withVehicle(vehicle, otherRightWheel, vehicleRightWheelContact.inverted()));
                if (first) return collisions;
            }
            var vehicleLeftWheelContact = ContactUtils.getCircleHalfCircleContact(otherLeftWheelShape, vehicleShape);
            if (vehicleLeftWheelContact != null) {
                collisions.add(Collision.withVehicle(vehicle, otherLeftWheel, vehicleLeftWheelContact.inverted()));
                if (first) return collisions;
            }
            var leftWheelVehicleContact = ContactUtils.getCircleHalfCircleContact(leftWheelShape, otherVehicleShape);
            if (leftWheelVehicleContact != null) {
                collisions.add(Collision.withVehicle(vehicle.getLeftWheel(), otherVehicle, leftWheelVehicleContact));
                if (first) return collisions;
            }
            var leftWheelLeftWheelContact = ContactUtils.getCirclesContact(leftWheelShape, otherLeftWheelShape);
            if (leftWheelLeftWheelContact != null) {
                collisions.add(Collision.withVehicle(vehicle.getLeftWheel(), otherLeftWheel, leftWheelLeftWheelContact));
                if (first) return collisions;
            }
            var rightWheelRightWheelContact = ContactUtils.getCirclesContact(rightWheelShape, otherRightWheelShape);
            if (rightWheelRightWheelContact != null) {
                collisions.add(Collision.withVehicle(vehicle.getRightWheel(), otherRightWheel, rightWheelRightWheelContact));
                if (first) return collisions;
            }
        }
        return collisions;
    }

    private static boolean collisionNotDetected(VehicleCalculations vehicle, VehicleCalculations otherVehicle) {
        return otherVehicle.getCollisions().stream()
                .noneMatch(c -> CollideObjectType.VEHICLE.equals(c.getType())
                        && c.getSecondId().equals(vehicle.getId()));
    }
}
