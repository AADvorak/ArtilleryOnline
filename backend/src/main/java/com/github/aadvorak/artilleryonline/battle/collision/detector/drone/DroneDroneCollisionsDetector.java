package com.github.aadvorak.artilleryonline.battle.collision.detector.drone;

import com.github.aadvorak.artilleryonline.battle.calculations.BattleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.Calculations;
import com.github.aadvorak.artilleryonline.battle.calculations.DroneCalculations;
import com.github.aadvorak.artilleryonline.battle.collision.Collision;
import com.github.aadvorak.artilleryonline.battle.collision.detector.CollisionsDetector;
import com.github.aadvorak.artilleryonline.battle.common.lines.Circle;
import com.github.aadvorak.artilleryonline.battle.utils.ContactUtils;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class DroneDroneCollisionsDetector implements CollisionsDetector {

    @Override
    public Set<Collision> detect(Calculations<?> calculations, BattleCalculations battle, boolean first) {
        if (calculations instanceof DroneCalculations droneCalculations) {
            return detect(droneCalculations, battle, first);
        }
        return Set.of();
    }

    private Set<Collision> detect(DroneCalculations drone, BattleCalculations battle, boolean first) {
        Set<Collision> collisions = new HashSet<>();
        var otherDrones = battle.getDrones().stream()
                .filter(value -> !Objects.equals(value.getId(), drone.getId())
                        && value.collisionsNotCheckedWith(drone.getId()))
                .collect(Collectors.toSet());
        if (otherDrones.isEmpty()) {
            return collisions;
        }
        var radius = drone.getModel().getSpecs().getHullRadius();
        var position = drone.getNext().getPosition();
        var shape = new Circle(position.getCenter(), radius);
        for (var otherDrone : otherDrones) {
            drone.addCollisionsCheckedWith(otherDrone.getId());
            otherDrone.addCollisionsCheckedWith(drone.getId());
            var otherRadius = otherDrone.getModel().getSpecs().getHullRadius();
            var otherPosition = otherDrone.getNext().getPosition();
            var otherShape = new Circle(otherPosition.getCenter(), otherRadius);
            var contact = ContactUtils.getCirclesContact(shape, otherShape);
            if (contact != null) {
                collisions.add(Collision.withDrone(drone, otherDrone, contact));
                if (first) return collisions;
            }
        }
        return collisions;
    }
}
