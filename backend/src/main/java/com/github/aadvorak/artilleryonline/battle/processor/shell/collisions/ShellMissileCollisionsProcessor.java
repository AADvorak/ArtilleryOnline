package com.github.aadvorak.artilleryonline.battle.processor.shell.collisions;

import com.github.aadvorak.artilleryonline.battle.calculations.BattleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.MissileCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.ShellCalculations;
import com.github.aadvorak.artilleryonline.battle.processor.damage.DamageProcessor;

public class ShellMissileCollisionsProcessor {

    public static void process(ShellCalculations shell, BattleCalculations battle) {
        var collision = ShellMissileCollisionsDetector.detectFirst(shell, battle);
        if (collision != null) {
            shell.getCollisions().add(collision);
            var missile = (MissileCalculations) collision.getPair().second();
            DamageProcessor.processHit(missile, battle);
            DamageProcessor.processHit(shell, battle);
            battle.getModel().getUpdates().removeMissile(missile.getId());
        }
    }
}
