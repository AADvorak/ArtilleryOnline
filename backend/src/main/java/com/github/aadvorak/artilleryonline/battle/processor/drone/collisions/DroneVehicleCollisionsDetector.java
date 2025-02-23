package com.github.aadvorak.artilleryonline.battle.processor.drone.collisions;

import com.github.aadvorak.artilleryonline.battle.calculations.*;
import com.github.aadvorak.artilleryonline.battle.common.Collision;
import com.github.aadvorak.artilleryonline.battle.common.lines.Circle;
import com.github.aadvorak.artilleryonline.battle.common.lines.HalfCircle;
import com.github.aadvorak.artilleryonline.battle.utils.InterpenetrationUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DroneVehicleCollisionsDetector {

    public static Collision detectFirst(DroneCalculations drone, BattleCalculations battle) {
        var collisions = detect(drone, battle, true);
        if (collisions.isEmpty()) {
            return null;
        }
        return collisions.iterator().next();
    }

    private static Set<Collision> detect(DroneCalculations drone, BattleCalculations battle, boolean first) {
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

    private static Collision detect(DroneCalculations drone, Calculations<?> other) {
        var radius = drone.getModel().getSpecs().getHullRadius();
        var position = drone.getNext().getPosition();
        var shape = new Circle(position.getCenter(), radius);
        if (other instanceof VehicleCalculations vehicle) {
            var otherRadius = vehicle.getModel().getSpecs().getRadius();
            var otherPosition = vehicle.getNextPosition();
            var otherShape = HalfCircle.of(otherPosition, otherRadius);
            var interpenetration = InterpenetrationUtils.getCircleHalfCircleInterpenetration(shape, otherShape);
            if (interpenetration != null) {
                return Collision.withVehicle(drone, vehicle, interpenetration);
            }
        }
        if (other instanceof WheelCalculations wheel) {
            var otherRadius = wheel.getModel().getSpecs().getWheelRadius();
            var otherPosition = wheel.getPosition();
            var otherShape = new Circle(otherPosition, otherRadius);
            var interpenetration = InterpenetrationUtils.getCirclesInterpenetration(shape, otherShape);
            if (interpenetration != null) {
                return Collision.withVehicle(drone, wheel, interpenetration);
            }
        }
        return null;
    }
}
