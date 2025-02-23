package com.github.aadvorak.artilleryonline.battle.processor.drone.collisions;

import com.github.aadvorak.artilleryonline.battle.calculations.BattleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.DroneCalculations;
import com.github.aadvorak.artilleryonline.battle.common.CollideObjectType;
import com.github.aadvorak.artilleryonline.battle.common.Collision;
import com.github.aadvorak.artilleryonline.battle.common.lines.Circle;
import com.github.aadvorak.artilleryonline.battle.utils.InterpenetrationUtils;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class DroneDroneCollisionsDetector {

    public static Collision detectFirst(DroneCalculations drone, BattleCalculations battle) {
        var collisions = detect(drone, battle, true);
        if (collisions.isEmpty()) {
            return null;
        }
        return collisions.iterator().next();
    }

    private static Set<Collision> detect(DroneCalculations drone, BattleCalculations battle, boolean first) {
        Set<Collision> collisions = new HashSet<>();
        var otherDrones = battle.getDrones().stream()
                .filter(value -> !Objects.equals(value.getId(), drone.getId()))
                .filter(value -> collisionNotDetected(drone, value.getId()))
                .collect(Collectors.toSet());
        if (otherDrones.isEmpty()) {
            return collisions;
        }
        var radius = drone.getModel().getSpecs().getHullRadius();
        var position = drone.getNext().getPosition();
        var shape = new Circle(position.getCenter(), radius);
        for (var otherDrone : otherDrones) {
            var otherRadius = otherDrone.getModel().getSpecs().getHullRadius();
            var otherPosition = otherDrone.getNext().getPosition();
            var otherShape = new Circle(otherPosition.getCenter(), otherRadius);
            var interpenetration = InterpenetrationUtils.getCirclesInterpenetration(shape, otherShape);
            if (interpenetration != null) {
                collisions.add(Collision.withDrone(drone, otherDrone, interpenetration));
                if (first) return collisions;
            }
        }
        return collisions;
    }

    private static boolean collisionNotDetected(DroneCalculations drone, Integer otherDroneId) {
        return drone.getCollisions().stream()
                .noneMatch(c -> CollideObjectType.DRONE.equals(c.getType())
                        && c.getSecondId().equals(otherDroneId));
    }
}
