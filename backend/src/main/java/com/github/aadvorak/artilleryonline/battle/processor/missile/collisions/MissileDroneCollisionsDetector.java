package com.github.aadvorak.artilleryonline.battle.processor.missile.collisions;

import com.github.aadvorak.artilleryonline.battle.calculations.BattleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.MissileCalculations;
import com.github.aadvorak.artilleryonline.battle.common.Collision;
import com.github.aadvorak.artilleryonline.battle.utils.CollisionUtils;

public class MissileDroneCollisionsDetector {

    public static Collision detectFirst(MissileCalculations missile, BattleCalculations battle) {
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
