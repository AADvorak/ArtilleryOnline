package com.github.aadvorak.artilleryonline.battle.collision.detector.shell;

import com.github.aadvorak.artilleryonline.battle.calculations.BattleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.Calculations;
import com.github.aadvorak.artilleryonline.battle.calculations.ShellCalculations;
import com.github.aadvorak.artilleryonline.battle.collision.detector.CollisionsDetector;
import com.github.aadvorak.artilleryonline.battle.common.Collision;
import com.github.aadvorak.artilleryonline.battle.utils.CollisionUtils;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class ShellMissileCollisionsDetector implements CollisionsDetector {

    @Override
    public Set<Collision> detect(Calculations<?> calculations, BattleCalculations battle, boolean first) {
        if (calculations instanceof ShellCalculations shell) {
            return detectFirst(shell, battle);
        }
        return Set.of();
    }

    private Set<Collision> detectFirst(ShellCalculations shell, BattleCalculations battle) {
        if (shell.getModel().getState().isStuck()) {
            return Set.of();
        }
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
