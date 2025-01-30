package com.github.aadvorak.artilleryonline.battle.processor.missile.collisions;

import com.github.aadvorak.artilleryonline.battle.calculations.BattleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.MissileCalculations;
import com.github.aadvorak.artilleryonline.battle.processor.damage.DamageProcessor;

public class MissileGroundCollisionProcessor {

    public static void process(MissileCalculations missile, BattleCalculations battle) {
        var collision = MissileGroundCollisionsDetector.detectFirst(missile, battle);
        if (collision != null) {
            missile.getCollisions().add(collision);
            DamageProcessor.processHitGround(missile, battle);
        }
    }
}
