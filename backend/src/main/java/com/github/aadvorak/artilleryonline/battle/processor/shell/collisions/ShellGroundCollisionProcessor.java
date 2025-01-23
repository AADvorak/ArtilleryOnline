package com.github.aadvorak.artilleryonline.battle.processor.shell.collisions;

import com.github.aadvorak.artilleryonline.battle.calculations.BattleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.ShellCalculations;
import com.github.aadvorak.artilleryonline.battle.processor.shell.ShellDamageProcessor;

public class ShellGroundCollisionProcessor {

    public static void process(ShellCalculations shell, BattleCalculations battle) {
        var collision = ShellGroundCollisionDetector.detectFirst(shell, battle);
        if (collision != null) {
            shell.getCollisions().add(collision);
            ShellDamageProcessor.processHitGround(shell.getNext().getPosition(), shell.getModel(), battle.getModel());
        }
    }
}
