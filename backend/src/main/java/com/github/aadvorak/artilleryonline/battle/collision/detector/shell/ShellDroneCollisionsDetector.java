package com.github.aadvorak.artilleryonline.battle.collision.detector.shell;

import com.github.aadvorak.artilleryonline.battle.calculations.BattleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.Calculations;
import com.github.aadvorak.artilleryonline.battle.calculations.ShellCalculations;
import com.github.aadvorak.artilleryonline.battle.collision.detector.CollisionsDetector;
import com.github.aadvorak.artilleryonline.battle.common.Collision;
import com.github.aadvorak.artilleryonline.battle.utils.CollisionUtils;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class ShellDroneCollisionsDetector implements CollisionsDetector {

    @Override
    public Set<Collision> detect(Calculations<?> calculations, BattleCalculations battle, boolean first) {
        if (calculations instanceof ShellCalculations shell) {
            return detectFirst(shell, battle);
        }
        return Set.of();
    }

    private Set<Collision> detectFirst(ShellCalculations shell, BattleCalculations battle) {
        var collisions = new HashSet<Collision>();
        if (shell.getModel().getState().isStuck()) {
            return collisions;
        }
        for (var drone : battle.getDrones()) {
            var collision = CollisionUtils.detectWithDrone(shell, shell.getPosition(),
                    shell.getNext().getPosition(), drone);
            if (collision != null) {
                collisions.add(collision);
                return collisions;
            }
        }
        return collisions;
    }
}
