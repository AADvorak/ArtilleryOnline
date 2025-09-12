package com.github.aadvorak.artilleryonline.battle.collision.postprocessor.box;

import com.github.aadvorak.artilleryonline.battle.calculations.BattleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.BoxCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.Calculations;
import com.github.aadvorak.artilleryonline.battle.collision.CollideObjectType;
import com.github.aadvorak.artilleryonline.battle.collision.postprocessor.CollisionPostprocessor;
import com.github.aadvorak.artilleryonline.battle.events.CollideEvent;
import com.github.aadvorak.artilleryonline.battle.model.BoxModel;
import com.github.aadvorak.artilleryonline.dto.response.CollisionResponse;
import org.springframework.stereotype.Component;

@Component
public class BoxCollisionPostprocessor implements CollisionPostprocessor {

    private static final long SOUND_DELAY = 1000;

    @Override
    public void process(Calculations<?> calculations, BattleCalculations battle) {
        if (calculations instanceof BoxCalculations box) {
            if (!box.getAllCollisions().isEmpty() && canPlaySound(box.getModel(), battle.getTime())) {
                box.getAllCollisions().stream()
                        .filter(collision -> collision.getClosingVelocity() > 1.0)
                        .forEach(collision -> {
                                    battle.getModel().getEvents().addCollide(
                                            new CollideEvent()
                                                    .setId(box.getModel().getId())
                                                    .setType(CollideObjectType.BOX)
                                                    .setObject(CollisionResponse.of(collision)));
                                    box.getModel().setLastSoundTime(battle.getTime());
                                }
                        );
            }
        }
    }

    private boolean canPlaySound(BoxModel model, long currentTime) {
        return currentTime - model.getLastSoundTime() > SOUND_DELAY;
    }
}
