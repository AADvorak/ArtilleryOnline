package com.github.aadvorak.artilleryonline.battle.collision.postprocessor.missile;

import com.github.aadvorak.artilleryonline.battle.calculations.BattleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.Calculations;
import com.github.aadvorak.artilleryonline.battle.calculations.MissileCalculations;
import com.github.aadvorak.artilleryonline.battle.collision.postprocessor.CollisionPostprocessor;
import org.springframework.stereotype.Component;

@Component
public class MissileCollisionPostprocessor implements CollisionPostprocessor {

    @Override
    public void process(Calculations<?> calculations, BattleCalculations battle) {
        if (calculations instanceof MissileCalculations missile) {
            if (!missile.getCollisions().isEmpty()) {
                battle.getModel().getUpdates().removeMissile(missile.getId());
            }
        }
    }
}
