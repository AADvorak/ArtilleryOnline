package com.github.aadvorak.artilleryonline.battle.collision.detector.shell;

import com.github.aadvorak.artilleryonline.battle.calculations.BattleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.Calculations;
import com.github.aadvorak.artilleryonline.battle.calculations.ShellCalculations;
import com.github.aadvorak.artilleryonline.battle.collision.detector.CollisionsDetector;
import com.github.aadvorak.artilleryonline.battle.common.Collision;

import java.util.Set;

public abstract class ShellCollisionDetectorBase implements CollisionsDetector {

    @Override
    public Set<Collision> detect(Calculations<?> calculations, BattleCalculations battle, boolean first) {
        if (calculations instanceof ShellCalculations shell) {
            if (shell.getModel().getState().isStuck()) {
                return Set.of();
            }
            return detectFirst(shell, battle);
        }
        return Set.of();
    }

    protected abstract Set<Collision> detectFirst(ShellCalculations shell, BattleCalculations battle);
}
