package com.github.aadvorak.artilleryonline.battle.processor.missile.collisions;

import com.github.aadvorak.artilleryonline.battle.calculations.BattleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.MissileCalculations;
import com.github.aadvorak.artilleryonline.battle.processor.damage.DamageProcessor;

public class MissileMissileCollisionsProcessor {

    public static void process(MissileCalculations missile, BattleCalculations battle) {
        var collision = MissileMissileCollisionsDetector.detectFirst(missile, battle);
        if (collision != null) {
            missile.getCollisions().add(collision);
            var otherMissile = (MissileCalculations) collision.getPair().second();
            otherMissile.getCollisions().add(collision.inverted());
            DamageProcessor.processHit(missile, battle);
            DamageProcessor.processHit(otherMissile, battle);
        }
    }
}
