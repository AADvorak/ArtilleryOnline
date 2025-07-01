package com.github.aadvorak.artilleryonline.battle.collision.detector.drone;

import com.github.aadvorak.artilleryonline.battle.calculations.*;
import com.github.aadvorak.artilleryonline.battle.collision.Collision;
import com.github.aadvorak.artilleryonline.battle.collision.detector.CollisionsDetector;
import com.github.aadvorak.artilleryonline.battle.common.lines.BodyPart;
import com.github.aadvorak.artilleryonline.battle.common.lines.Circle;
import com.github.aadvorak.artilleryonline.battle.utils.ContactUtils;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class DroneVehicleCollisionsDetector implements CollisionsDetector {

    @Override
    public Set<Collision> detect(Calculations<?> calculations, BattleCalculations battle, boolean first) {
        if (calculations instanceof DroneCalculations droneCalculations) {
            return detect(droneCalculations, battle, first);
        }
        return Set.of();
    }

    private Set<Collision> detect(DroneCalculations drone, BattleCalculations battle, boolean first) {
        Set<Collision> collisions = new HashSet<>();
        for (var vehicle : battle.getVehicles()) {
            for (var other : List.of(vehicle, vehicle.getLeftWheel(), vehicle.getRightWheel())) {
                var collision = detect(drone, other);
                if (collision != null) {
                    collisions.add(collision);
                    if (first) return collisions;
                }
            }
        }
        return collisions;
    }

    private Collision detect(DroneCalculations drone, Calculations<?> other) {
        var radius = drone.getModel().getSpecs().getHullRadius();
        var position = drone.getNext().getPosition();
        var dronePart = new Circle(position.getCenter(), radius);
        if (other instanceof VehicleCalculations vehicle) {
            var otherPosition = vehicle.getGeometryNextPosition();
            var otherPart = BodyPart.of(otherPosition, vehicle.getModel().getSpecs().getTurretShape());
            var contact = ContactUtils.getBodyPartsContact(dronePart, otherPart);
            if (contact != null) {
                return Collision.withVehicle(drone, vehicle, contact);
            }
        }
        if (other instanceof WheelCalculations wheel) {
            var otherRadius = wheel.getModel().getSpecs().getWheelRadius();
            var otherPosition = wheel.getNext().getPosition();
            var otherPart = new Circle(otherPosition, otherRadius);
            var contact = ContactUtils.getCirclesContact(dronePart, otherPart);
            if (contact != null) {
                return Collision.withVehicle(drone, wheel, contact);
            }
        }
        return null;
    }
}
