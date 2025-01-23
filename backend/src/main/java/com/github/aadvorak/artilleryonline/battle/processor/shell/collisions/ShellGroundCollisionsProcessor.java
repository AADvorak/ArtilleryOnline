package com.github.aadvorak.artilleryonline.battle.processor.shell.collisions;

import com.github.aadvorak.artilleryonline.battle.calculations.BattleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.ShellCalculations;
import com.github.aadvorak.artilleryonline.battle.processor.damage.DamageProcessor;

public class ShellGroundCollisionsProcessor {

    public static void process(ShellCalculations shell, BattleCalculations battle) {
        var collision = ShellGroundCollisionsDetector.detectFirst(shell, battle);
        if (collision != null) {
            shell.getCollisions().add(collision);
            DamageProcessor.processHitGround(shell, battle);
        }
    }
}
