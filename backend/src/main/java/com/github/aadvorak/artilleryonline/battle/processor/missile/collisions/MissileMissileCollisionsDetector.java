package com.github.aadvorak.artilleryonline.battle.processor.missile.collisions;

import com.github.aadvorak.artilleryonline.battle.calculations.BattleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.MissileCalculations;
import com.github.aadvorak.artilleryonline.battle.common.Collision;
import com.github.aadvorak.artilleryonline.battle.utils.CollisionUtils;

public class MissileMissileCollisionsDetector {

    public static Collision detectFirst(MissileCalculations missile, BattleCalculations battle) {
        for (var otherMissile : battle.getMissiles()) {
            // todo we take tail and head positions instead of current and next positions, so collision angle is wrong
            var collision = CollisionUtils.detectWithMissile(missile, missile.getPositions().getTail(),
                    missile.getPositions().getHead(), otherMissile);
            if (collision != null) {
                return collision;
            }
        }
        return null;
    }
}
