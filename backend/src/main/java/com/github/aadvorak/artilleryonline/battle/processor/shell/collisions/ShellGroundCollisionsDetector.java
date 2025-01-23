package com.github.aadvorak.artilleryonline.battle.processor.shell.collisions;

import com.github.aadvorak.artilleryonline.battle.calculations.BattleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.ShellCalculations;
import com.github.aadvorak.artilleryonline.battle.common.Collision;
import com.github.aadvorak.artilleryonline.battle.utils.BattleUtils;

public class ShellGroundCollisionsDetector {

    public static Collision detectFirst(ShellCalculations shell, BattleCalculations battle) {
        var nearestGroundPosition = BattleUtils.getNearestGroundPosition(shell.getPosition().getX(),
                battle.getModel().getRoom());
        if (shell.getPosition().getY() <= nearestGroundPosition.getY()) {
            return Collision.ofShellWithGround(shell);
        }
        return null;
    }
}
