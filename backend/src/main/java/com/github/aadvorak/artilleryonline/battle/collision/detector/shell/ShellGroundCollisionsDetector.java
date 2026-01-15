package com.github.aadvorak.artilleryonline.battle.collision.detector.shell;

import com.github.aadvorak.artilleryonline.battle.calculations.BattleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.ShellCalculations;
import com.github.aadvorak.artilleryonline.battle.collision.Collision;
import com.github.aadvorak.artilleryonline.battle.common.Contact;
import com.github.aadvorak.artilleryonline.battle.common.lines.Segment;
import com.github.aadvorak.artilleryonline.battle.utils.BattleUtils;
import org.springframework.stereotype.Component;

@Component
public class ShellGroundCollisionsDetector extends ShellCollisionsDetectorBase {

    protected Collision detectFirst(ShellCalculations shell, BattleCalculations battle) {
        var shellTrajectory = new Segment(shell.getPosition(), shell.getNext().getPosition());
        var intersectionPoint = BattleUtils.getGroundIntersectionPoint(shellTrajectory, battle.getModel().getRoom());
        if (intersectionPoint != null) {
            var normal = shell.getPosition().vectorTo(intersectionPoint).normalized();
            return Collision.withGround(shell,
                    Contact.withUncheckedDepthOf(0.0, normal, intersectionPoint));
        }
        return null;
    }
}
