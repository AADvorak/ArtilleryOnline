package com.github.aadvorak.artilleryonline.battle.collision.detector.shell;

import com.github.aadvorak.artilleryonline.battle.calculations.BattleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.Calculations;
import com.github.aadvorak.artilleryonline.battle.calculations.ShellCalculations;
import com.github.aadvorak.artilleryonline.battle.collision.detector.CollisionsDetector;
import com.github.aadvorak.artilleryonline.battle.collision.Collision;

import java.util.Set;

public abstract class ShellCollisionsDetectorBase implements CollisionsDetector {

    @Override
    public Set<Collision> detect(Calculations<?> calculations, BattleCalculations battle, boolean first) {
        if (calculations instanceof ShellCalculations shell) {
            if (shell.getModel().getState().isStuck() || !shell.getAllCollisions().isEmpty()) {
                return Set.of();
            }
            var collision = detectFirst(shell, battle);
            if (collision != null) {
                return Set.of(collision);
            }
        }
        return Set.of();
    }

    protected abstract Collision detectFirst(ShellCalculations shell, BattleCalculations battle);
}
