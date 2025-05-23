package com.github.aadvorak.artilleryonline.battle.collision.detector.missile;

import com.github.aadvorak.artilleryonline.battle.calculations.BattleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.Calculations;
import com.github.aadvorak.artilleryonline.battle.calculations.MissileCalculations;
import com.github.aadvorak.artilleryonline.battle.collision.detector.CollisionsDetector;
import com.github.aadvorak.artilleryonline.battle.collision.Collision;

import java.util.Set;

public abstract class MissileCollisionsDetectorBase implements CollisionsDetector {

    @Override
    public Set<Collision> detect(Calculations<?> calculations, BattleCalculations battle, boolean first) {
        if (calculations instanceof MissileCalculations missile) {
            var collision = detectFirst(missile, battle);
            if (collision != null) {
                return Set.of(collision);
            }
        }
        return Set.of();
    }

    protected abstract Collision detectFirst(MissileCalculations missile, BattleCalculations battle);
}
