package com.github.aadvorak.artilleryonline.battle.processor.shell.collisions;

import com.github.aadvorak.artilleryonline.battle.calculations.BattleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.ShellCalculations;
import com.github.aadvorak.artilleryonline.battle.common.ShellType;
import com.github.aadvorak.artilleryonline.battle.processor.damage.DamageProcessor;
import com.github.aadvorak.artilleryonline.battle.processor.shell.BombDropProcessor;

public class ShellGroundCollisionsProcessor {

    public static void process(ShellCalculations shell, BattleCalculations battle) {
        var collision = ShellGroundCollisionsDetector.detectFirst(shell, battle);
        if (collision != null) {
            if (ShellType.SGN.equals(shell.getModel().getSpecs().getType())) {
                shell.getModel().getState().setStuck(true);
                shell.applyNextPosition();
                shell.getModel().setUpdated(true);
                BombDropProcessor.flyEvent(shell.getPosition(), shell.getModel().getVehicleId(), battle.getModel());
            } else {
                shell.getCollisions().add(collision);
                DamageProcessor.processHit(shell, battle);
            }
        }
    }
}
