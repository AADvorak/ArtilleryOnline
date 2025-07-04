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
import java.util.Map;
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
        var parts = Map.of(
                BodyPart.of(position, vehicle.getModel().getSpecs().getTurretShape()), vehicle,
                new Circle(rightWheelPosition, wheelRadius), vehicle.getRightWheel(),
                new Circle(leftWheelPosition, wheelRadius), vehicle.getLeftWheel()
        );
        for (var otherVehicle : otherVehicles) {
            var otherMaxRadius = otherVehicle.getModel().getPreCalc().getMaxRadius();
            var otherPosition = otherVehicle.getGeometryNextPosition();
            if (otherPosition.getCenter().distanceTo(position.getCenter()) > maxRadius + otherMaxRadius) {
                continue;
            }
            var otherWheelRadius = otherVehicle.getModel().getSpecs().getWheelRadius();
            var otherLeftWheelPosition = otherVehicle.getLeftWheel().getNext().getPosition();
            var otherRightWheelPosition = otherVehicle.getRightWheel().getNext().getPosition();
            var otherParts = Map.of(
                    BodyPart.of(otherPosition, otherVehicle.getModel().getSpecs().getTurretShape()), otherVehicle,
                    new Circle(otherLeftWheelPosition, otherWheelRadius), otherVehicle.getLeftWheel(),
                    new Circle(otherRightWheelPosition, otherWheelRadius), otherVehicle.getRightWheel()
            );
            for (var part : parts.entrySet()) {
                for (var otherPart : otherParts.entrySet()) {
                    var contact = ContactUtils.getBodyPartsContact(part.getKey(), otherPart.getKey());
                    if (contact != null) {
                        collisions.add(Collision.withVehicle(part.getValue(), otherPart.getValue(), contact));
                        if (first) return collisions;
                    }
                }
            }
        }
        return collisions;
    }
}
