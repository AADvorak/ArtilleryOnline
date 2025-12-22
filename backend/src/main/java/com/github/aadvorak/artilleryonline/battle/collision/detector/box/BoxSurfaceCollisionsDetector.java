package com.github.aadvorak.artilleryonline.battle.collision.detector.box;

import com.github.aadvorak.artilleryonline.battle.calculations.BattleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.BoxCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.Calculations;
import com.github.aadvorak.artilleryonline.battle.collision.Collision;
import com.github.aadvorak.artilleryonline.battle.collision.detector.CollisionsDetector;
import com.github.aadvorak.artilleryonline.battle.common.lines.BodyPart;
import com.github.aadvorak.artilleryonline.battle.utils.SurfaceContactUtils;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class BoxSurfaceCollisionsDetector implements CollisionsDetector {

    @Override
    public Set<Collision> detect(Calculations<?> calculations, BattleCalculations battle, boolean first) {
        if (calculations instanceof BoxCalculations boxCalculations) {
            return detect(boxCalculations, battle);
        }
        return Set.of();
    }

    private Set<Collision> detect(BoxCalculations box, BattleCalculations battle) {
        var bodyPart = BodyPart.of(box.getGeometryNextPosition(), box.getModel().getSpecs().getShape());
        return SurfaceContactUtils.getContacts(bodyPart,
                        battle.getModel().getRoom(), true).stream()
                .map(contact -> Collision.withSurface(box, contact))
                .collect(java.util.stream.Collectors.toSet());
    }
}
