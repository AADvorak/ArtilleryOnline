package com.github.aadvorak.artilleryonline.battle.collision.detector.drone;

import com.github.aadvorak.artilleryonline.battle.calculations.BattleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.BoxCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.Calculations;
import com.github.aadvorak.artilleryonline.battle.calculations.DroneCalculations;
import com.github.aadvorak.artilleryonline.battle.collision.Collision;
import com.github.aadvorak.artilleryonline.battle.collision.detector.CollisionsDetector;
import com.github.aadvorak.artilleryonline.battle.common.lines.BodyPart;
import com.github.aadvorak.artilleryonline.battle.common.lines.Circle;
import com.github.aadvorak.artilleryonline.battle.utils.ContactUtils;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class DroneBoxCollisionsDetector implements CollisionsDetector {

    @Override
    public Set<Collision> detect(Calculations<?> calculations, BattleCalculations battle, boolean first) {
        if (calculations instanceof DroneCalculations droneCalculations) {
            return detect(droneCalculations, battle, first);
        }
        return Set.of();
    }

    private Set<Collision> detect(DroneCalculations drone, BattleCalculations battle, boolean first) {
        Set<Collision> collisions = new HashSet<>();
        for (var box : battle.getBoxes()) {
            var collision = detect(drone, box);
            if (collision != null) {
                collisions.add(collision);
                if (first) return collisions;
            }
        }
        return collisions;
    }

    private Collision detect(DroneCalculations drone, BoxCalculations box) {
        var radius = drone.getModel().getSpecs().getHullRadius();
        var position = drone.getNext().getPosition();
        var otherPosition = box.getGeometryNextPosition();
        var maxRadius = drone.getModel().getPreCalc().getMaxRadius();
        var otherMaxRadius = box.getModel().getPreCalc().getMaxRadius();
        if (otherPosition.getCenter().distanceTo(box.getNext().getPosition().getCenter())
                > maxRadius + otherMaxRadius) {
            return null;
        }
        var dronePart = new Circle(position.getCenter(), radius);
        var otherPart = BodyPart.of(otherPosition, box.getModel().getSpecs().getShape());
        var contact = ContactUtils.getBodyPartsContact(dronePart, otherPart);
        if (contact != null) {
            return Collision.withBox(drone, box, contact);
        }
        return null;
    }
}
