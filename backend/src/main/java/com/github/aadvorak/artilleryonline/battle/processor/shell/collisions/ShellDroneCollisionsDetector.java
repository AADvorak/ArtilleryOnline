package com.github.aadvorak.artilleryonline.battle.processor.shell.collisions;

import com.github.aadvorak.artilleryonline.battle.calculations.BattleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.ShellCalculations;
import com.github.aadvorak.artilleryonline.battle.common.Collision;
import com.github.aadvorak.artilleryonline.battle.utils.CollisionUtils;

public class ShellDroneCollisionsDetector {

    public static Collision detectFirst(ShellCalculations shell, BattleCalculations battle) {
        for (var drone : battle.getDrones()) {
            var collision = CollisionUtils.detectWithDrone(shell, shell.getPosition(),
                    shell.getNext().getPosition(), drone);
            if (collision != null) {
                return collision;
            }
        }
        return null;
    }
}
