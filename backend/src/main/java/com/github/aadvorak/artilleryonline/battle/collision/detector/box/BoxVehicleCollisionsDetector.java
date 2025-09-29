package com.github.aadvorak.artilleryonline.battle.collision.detector.box;

import com.github.aadvorak.artilleryonline.battle.calculations.BattleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.BoxCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.Calculations;
import com.github.aadvorak.artilleryonline.battle.collision.Collision;
import com.github.aadvorak.artilleryonline.battle.collision.detector.CollisionsDetector;
import com.github.aadvorak.artilleryonline.battle.common.lines.BodyPart;
import com.github.aadvorak.artilleryonline.battle.common.lines.Circle;
import com.github.aadvorak.artilleryonline.battle.utils.ContactUtils;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Component
public class BoxVehicleCollisionsDetector implements CollisionsDetector {

    @Override
    public Set<Collision> detect(Calculations<?> calculations, BattleCalculations battle, boolean first) {
        if (calculations instanceof BoxCalculations boxCalculations) {
            return detect(boxCalculations, battle, first);
        }
        return Set.of();
    }

    private Set<Collision> detect(BoxCalculations box, BattleCalculations battle, boolean first) {
        Set<Collision> collisions = new HashSet<>();
        var maxRadius = box.getModel().getPreCalc().getMaxRadius();
        var bodyPart = BodyPart.of(box.getGeometryNextPosition(), box.getModel().getSpecs().getShape());
        for (var vehicle : battle.getVehicles()) {
            var otherMaxRadius = vehicle.getModel().getPreCalc().getMaxRadius();
            var otherPosition = vehicle.getGeometryNextPosition();
            if (otherPosition.getCenter().distanceTo(box.getNext().getPosition().getCenter())
                    > maxRadius + otherMaxRadius) {
                continue;
            }
            var otherWheelRadius = vehicle.getModel().getSpecs().getWheelRadius();
            var otherLeftWheelPosition = vehicle.getLeftWheel().getNext().getPosition();
            var otherRightWheelPosition = vehicle.getRightWheel().getNext().getPosition();
            var otherParts = Map.of(
                    BodyPart.of(otherPosition, vehicle.getModel().getSpecs().getTurretShape()), vehicle,
                    new Circle(otherLeftWheelPosition, otherWheelRadius), vehicle.getLeftWheel(),
                    new Circle(otherRightWheelPosition, otherWheelRadius), vehicle.getRightWheel()
            );
            for (var otherPart : otherParts.entrySet()) {
                var contact = ContactUtils.getBodyPartsContact(bodyPart, otherPart.getKey());
                if (contact != null) {
                    collisions.add(Collision.withVehicle(box, otherPart.getValue(), contact));
                    if (first) return collisions;
                }
            }
        }
        return collisions;
    }
}
