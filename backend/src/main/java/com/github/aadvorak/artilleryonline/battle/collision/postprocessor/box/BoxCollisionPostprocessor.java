package com.github.aadvorak.artilleryonline.battle.collision.postprocessor.box;

import com.github.aadvorak.artilleryonline.battle.calculations.BattleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.BoxCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.Calculations;
import com.github.aadvorak.artilleryonline.battle.collision.CollideObjectType;
import com.github.aadvorak.artilleryonline.battle.collision.postprocessor.CollisionPostprocessor;
import com.github.aadvorak.artilleryonline.battle.events.CollideEvent;
import com.github.aadvorak.artilleryonline.dto.response.CollisionResponse;
import org.springframework.stereotype.Component;

@Component
public class BoxCollisionPostprocessor implements CollisionPostprocessor {

    @Override
    public void process(Calculations<?> calculations, BattleCalculations battle) {
        if (calculations instanceof BoxCalculations box) {
            if (!box.getAllCollisions().isEmpty()) {
                box.getAllCollisions().stream()
                        .filter(collision -> collision.getClosingVelocity() > 1.0)
                        .forEach(collision -> battle.getModel().getEvents().addCollide(
                                new CollideEvent()
                                        .setId(box.getModel().getId())
                                        .setType(CollideObjectType.BOX)
                                        .setObject(CollisionResponse.of(collision)))
                        );
            }
        }
    }
}
