package com.github.aadvorak.artilleryonline.battle.collision.detector.shell;

import com.github.aadvorak.artilleryonline.battle.calculations.BattleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.ShellCalculations;
import com.github.aadvorak.artilleryonline.battle.common.Collision;
import com.github.aadvorak.artilleryonline.battle.utils.CollisionUtils;
import org.springframework.stereotype.Component;

@Component
public class ShellMissileCollisionsDetector extends ShellCollisionsDetectorBase {

    protected Collision detectFirst(ShellCalculations shell, BattleCalculations battle) {
        for (var missile: battle.getMissiles()) {
            var collision = CollisionUtils.detectWithMissile(shell, shell.getPosition(),
                    shell.getNext().getPosition(), missile);
            if (collision != null) {
                return collision;
            }
         }
         return null;
    }
}
