package com.github.aadvorak.artilleryonline.battle.collision.detector.shell;

import com.github.aadvorak.artilleryonline.battle.calculations.BattleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.Calculations;
import com.github.aadvorak.artilleryonline.battle.calculations.ShellCalculations;
import com.github.aadvorak.artilleryonline.battle.collision.detector.CollisionsDetector;
import com.github.aadvorak.artilleryonline.battle.common.Collision;
import com.github.aadvorak.artilleryonline.battle.common.Contact;
import com.github.aadvorak.artilleryonline.battle.common.lines.Segment;
import com.github.aadvorak.artilleryonline.battle.utils.BattleUtils;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class ShellGroundCollisionsDetector implements CollisionsDetector {

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
        var shellTrajectory = new Segment(shell.getPosition(), shell.getNext().getPosition());
        var firstPointUnderGround = BattleUtils.getFirstPointUnderGround(shellTrajectory, battle.getModel().getRoom());
        if (firstPointUnderGround != null) {
            var normal = shell.getPosition().vectorTo(firstPointUnderGround).normalized();
            return Set.of(Collision.withGround(shell,
                    Contact.withUncheckedDepthOf(0.0, normal, firstPointUnderGround)));
        }
        return Set.of();
    }
}
