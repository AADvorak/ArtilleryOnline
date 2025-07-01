package com.github.aadvorak.artilleryonline.battle.collision.detector.vehicle;

import com.github.aadvorak.artilleryonline.battle.calculations.BattleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.Calculations;
import com.github.aadvorak.artilleryonline.battle.calculations.VehicleCalculations;
import com.github.aadvorak.artilleryonline.battle.collision.Collision;
import com.github.aadvorak.artilleryonline.battle.collision.detector.CollisionsDetector;
import com.github.aadvorak.artilleryonline.battle.common.lines.BodyPart;
import com.github.aadvorak.artilleryonline.battle.common.lines.Circle;
import com.github.aadvorak.artilleryonline.battle.utils.CollisionUtils;
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
                .filter(value -> CollisionUtils.collisionNotDetected(vehicle, value))
                .collect(Collectors.toSet());
        var wheelRadius = vehicle.getModel().getSpecs().getWheelRadius();
        var maxRadius = vehicle.getModel().getPreCalc().getMaxRadius();
        var position = vehicle.getGeometryNextPosition();
        var rightWheelPosition = vehicle.getRightWheel().getNext().getPosition();
        var leftWheelPosition = vehicle.getLeftWheel().getNext().getPosition();
        var vehiclePart = BodyPart.of(position, vehicle.getModel().getSpecs().getTurretShape());
        var rightWheelPart = new Circle(rightWheelPosition, wheelRadius);
        var leftWheelPart = new Circle(leftWheelPosition, wheelRadius);
        for (var otherVehicle : otherVehicles) {
            var otherMaxRadius = otherVehicle.getModel().getPreCalc().getMaxRadius();
            var otherPosition = otherVehicle.getGeometryNextPosition();
            if (otherPosition.getCenter().distanceTo(position.getCenter()) > maxRadius + otherMaxRadius) {
                continue;
            }
            var otherVehiclePart = BodyPart.of(otherPosition, otherVehicle.getModel().getSpecs().getTurretShape());
            var vehicleVehicleContact = ContactUtils.getBodyPartsContact(vehiclePart, otherVehiclePart);
            if (vehicleVehicleContact != null) {
                collisions.add(Collision.withVehicle(vehicle, otherVehicle, vehicleVehicleContact));
                if (first) return collisions;
            }
            var otherWheelRadius = otherVehicle.getModel().getSpecs().getWheelRadius();
            var otherLeftWheelPosition = otherVehicle.getLeftWheel().getNext().getPosition();
            var otherRightWheelPosition = otherVehicle.getRightWheel().getNext().getPosition();
            var otherLeftWheel = otherVehicle.getLeftWheel();
            var otherRightWheel = otherVehicle.getRightWheel();
            var otherLeftWheelPart = new Circle(otherLeftWheelPosition, otherWheelRadius);
            var otherRightWheelPart = new Circle(otherRightWheelPosition, otherWheelRadius);
            var rightWheelLeftWheelContact = ContactUtils.getCirclesContact(rightWheelPart, otherLeftWheelPart);
            if (rightWheelLeftWheelContact != null) {
                collisions.add(Collision.withVehicle(vehicle.getRightWheel(), otherLeftWheel, rightWheelLeftWheelContact));
                if (first) return collisions;
            }
            var leftWheelRightWheelContact = ContactUtils.getCirclesContact(leftWheelPart, otherRightWheelPart);
            if (leftWheelRightWheelContact != null) {
                collisions.add(Collision.withVehicle(vehicle.getLeftWheel(), otherRightWheel, leftWheelRightWheelContact));
                if (first) return collisions;
            }
            var rightWheelVehicleContact = ContactUtils.getBodyPartsContact(rightWheelPart, otherVehiclePart);
            if (rightWheelVehicleContact != null) {
                collisions.add(Collision.withVehicle(vehicle.getRightWheel(), otherVehicle, rightWheelVehicleContact));
                if (first) return collisions;
            }
            var vehicleRightWheelContact = ContactUtils.getBodyPartsContact(vehiclePart, otherRightWheelPart);
            if (vehicleRightWheelContact != null) {
                collisions.add(Collision.withVehicle(vehicle, otherRightWheel, vehicleRightWheelContact));
                if (first) return collisions;
            }
            var vehicleLeftWheelContact = ContactUtils.getBodyPartsContact(vehiclePart, otherLeftWheelPart);
            if (vehicleLeftWheelContact != null) {
                collisions.add(Collision.withVehicle(vehicle, otherLeftWheel, vehicleLeftWheelContact));
                if (first) return collisions;
            }
            var leftWheelVehicleContact = ContactUtils.getBodyPartsContact(leftWheelPart, otherVehiclePart);
            if (leftWheelVehicleContact != null) {
                collisions.add(Collision.withVehicle(vehicle.getLeftWheel(), otherVehicle, leftWheelVehicleContact));
                if (first) return collisions;
            }
            var leftWheelLeftWheelContact = ContactUtils.getCirclesContact(leftWheelPart, otherLeftWheelPart);
            if (leftWheelLeftWheelContact != null) {
                collisions.add(Collision.withVehicle(vehicle.getLeftWheel(), otherLeftWheel, leftWheelLeftWheelContact));
                if (first) return collisions;
            }
            var rightWheelRightWheelContact = ContactUtils.getCirclesContact(rightWheelPart, otherRightWheelPart);
            if (rightWheelRightWheelContact != null) {
                collisions.add(Collision.withVehicle(vehicle.getRightWheel(), otherRightWheel, rightWheelRightWheelContact));
                if (first) return collisions;
            }
        }
        return collisions;
    }
}
