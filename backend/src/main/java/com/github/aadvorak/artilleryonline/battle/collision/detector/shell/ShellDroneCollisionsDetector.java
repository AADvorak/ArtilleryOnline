package com.github.aadvorak.artilleryonline.battle.collision.detector.shell;

import com.github.aadvorak.artilleryonline.battle.calculations.BattleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.ShellCalculations;
import com.github.aadvorak.artilleryonline.battle.common.Collision;
import com.github.aadvorak.artilleryonline.battle.utils.CollisionUtils;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class ShellDroneCollisionsDetector extends ShellCollisionDetectorBase {

    protected Set<Collision> detectFirst(ShellCalculations shell, BattleCalculations battle) {
        var collisions = new HashSet<Collision>();
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
