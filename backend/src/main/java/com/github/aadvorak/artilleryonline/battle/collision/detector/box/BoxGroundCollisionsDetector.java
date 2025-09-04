package com.github.aadvorak.artilleryonline.battle.collision.detector.box;

import com.github.aadvorak.artilleryonline.battle.calculations.BattleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.BoxCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.Calculations;
import com.github.aadvorak.artilleryonline.battle.collision.Collision;
import com.github.aadvorak.artilleryonline.battle.collision.detector.CollisionsDetector;
import com.github.aadvorak.artilleryonline.battle.common.lines.BodyPart;
import com.github.aadvorak.artilleryonline.battle.utils.GroundContactUtils;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class BoxGroundCollisionsDetector implements CollisionsDetector {

    @Override
    public Set<Collision> detect(Calculations<?> calculations, BattleCalculations battle, boolean first) {
        if (calculations instanceof BoxCalculations boxCalculations) {
            return detect(boxCalculations, battle, first);
        }
        return Set.of();
    }

    private Set<Collision> detect(BoxCalculations box, BattleCalculations battle, boolean first) {
        // todo wall
        return detectGroundCollision(box, battle);
    }

    private Set<Collision> detectGroundCollision(BoxCalculations box, BattleCalculations battle) {
        return GroundContactUtils.getGroundContacts(
                BodyPart.of(box.getGeometryNextPosition(), box.getModel().getSpecs().getShape()),
                battle.getModel().getRoom(), true).stream()
                .map(contact -> Collision.withGround(box, contact))
                .collect(Collectors.toSet());
    }
}
