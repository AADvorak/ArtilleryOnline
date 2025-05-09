package com.github.aadvorak.artilleryonline.battle.collision.detector.shell;

import com.github.aadvorak.artilleryonline.battle.calculations.BattleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.ShellCalculations;
import com.github.aadvorak.artilleryonline.battle.common.Collision;
import com.github.aadvorak.artilleryonline.battle.utils.CollisionUtils;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class ShellMissileCollisionsDetector extends ShellCollisionDetectorBase {

    protected Set<Collision> detectFirst(ShellCalculations shell, BattleCalculations battle) {
        for (var missile: battle.getMissiles()) {
            var collision = CollisionUtils.detectWithMissile(shell, shell.getPosition(),
                    shell.getNext().getPosition(), missile);
            if (collision != null) {
                return Set.of(collision);
            }
         }
         return Set.of();
    }
}
