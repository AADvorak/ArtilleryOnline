package com.github.aadvorak.artilleryonline.battle.collision.detector.missile;

import com.github.aadvorak.artilleryonline.battle.calculations.BattleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.MissileCalculations;
import com.github.aadvorak.artilleryonline.battle.collision.Collision;
import com.github.aadvorak.artilleryonline.battle.utils.CollisionUtils;
import org.springframework.stereotype.Component;

@Component
public class MissileDroneCollisionsDetector extends MissileCollisionsDetectorBase {

    protected Collision detectFirst(MissileCalculations missile, BattleCalculations battle) {
        for (var drone : battle.getDrones()) {
            var collision = CollisionUtils.detectWithDrone(missile, missile.getPositions().getTail(),
                    missile.getPositions().getHead(), drone);
            if (collision != null) {
                return collision;
            }
        }
        return null;
    }
}
