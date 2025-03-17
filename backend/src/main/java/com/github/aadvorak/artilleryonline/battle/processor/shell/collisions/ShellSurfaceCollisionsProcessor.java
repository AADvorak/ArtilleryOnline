package com.github.aadvorak.artilleryonline.battle.processor.shell.collisions;

import com.github.aadvorak.artilleryonline.battle.calculations.BattleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.ShellCalculations;
import com.github.aadvorak.artilleryonline.battle.common.VectorProjections;

public class ShellSurfaceCollisionsProcessor {

    public static void process(ShellCalculations shell, BattleCalculations battle) {
        var collision = ShellSurfaceCollisionsDetector.detectFirst(shell, battle);
        if (collision != null) {
            var velocityProjections = VectorProjections.copyOf(collision.getVelocitiesProjections().first());
            velocityProjections.setNormal(-velocityProjections.getNormal());
            collision.getPair().first().setVelocity(velocityProjections.recoverVelocity());
            collision.getPair().first().calculateNextPosition(battle.getModel().getCurrentTimeStepSecs());
            shell.getModel().setUpdated(true);
        }
    }
}
