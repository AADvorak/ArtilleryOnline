package com.github.aadvorak.artilleryonline.battle.collision.detector.shell;

import com.github.aadvorak.artilleryonline.battle.calculations.BattleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.ShellCalculations;
import com.github.aadvorak.artilleryonline.battle.collision.Collision;
import com.github.aadvorak.artilleryonline.battle.utils.CollisionUtils;
import org.springframework.stereotype.Component;

@Component
public class ShellBoxCollisionsDetector extends ShellCollisionsDetectorBase {

    protected Collision detectFirst(ShellCalculations shell, BattleCalculations battle) {
        for (var box : battle.getBoxes()) {
            var collision = CollisionUtils.detectWithBox(shell, shell.getPosition(),
                    shell.getNext().getPosition(), box);
            if (collision != null) {
                return collision;
            }
        }
        return null;
    }
}
