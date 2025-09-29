package com.github.aadvorak.artilleryonline.battle.collision.detector.box;

import com.github.aadvorak.artilleryonline.battle.calculations.BattleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.BoxCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.Calculations;
import com.github.aadvorak.artilleryonline.battle.collision.Collision;
import com.github.aadvorak.artilleryonline.battle.collision.detector.CollisionsDetector;
import com.github.aadvorak.artilleryonline.battle.common.lines.BodyPart;
import com.github.aadvorak.artilleryonline.battle.utils.CollisionUtils;
import com.github.aadvorak.artilleryonline.battle.utils.ContactUtils;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class BoxBoxCollisionsDetector implements CollisionsDetector {

    @Override
    public Set<Collision> detect(Calculations<?> calculations, BattleCalculations battle, boolean first) {
        if (calculations instanceof BoxCalculations boxCalculations) {
            return detect(boxCalculations, battle, first);
        }
        return Set.of();
    }

    private Set<Collision> detect(BoxCalculations box, BattleCalculations battle, boolean first) {
        Set<Collision> collisions = new HashSet<>();
        var otherBoxes = battle.getBoxes().stream()
                .filter(value -> !Objects.equals(value.getId(), box.getId()))
                .filter(value -> CollisionUtils.collisionNotDetected(box, value))
                .collect(Collectors.toSet());
        var maxRadius = box.getModel().getPreCalc().getMaxRadius();
        var position = box.getPosition();
        var geometryPosition = box.getGeometryNextPosition();
        var bodyPart = BodyPart.of(geometryPosition, box.getModel().getSpecs().getShape());
        for (var otherBox : otherBoxes) {
            var otherMaxRadius = otherBox.getModel().getPreCalc().getMaxRadius();
            var otherPosition = otherBox.getPosition();
            var otherGeometryPosition = otherBox.getGeometryNextPosition();
            if (otherPosition.distanceTo(position) > maxRadius + otherMaxRadius) {
                continue;
            }
            var otherBodyPart = BodyPart.of(otherGeometryPosition, otherBox.getModel().getSpecs().getShape());
            var contact = ContactUtils.getBodyPartsContact(bodyPart, otherBodyPart);
            if (contact != null) {
                collisions.add(Collision.withBox(box, otherBox, contact));
                if (first) return collisions;
            }
        }
        return collisions;
    }
}
