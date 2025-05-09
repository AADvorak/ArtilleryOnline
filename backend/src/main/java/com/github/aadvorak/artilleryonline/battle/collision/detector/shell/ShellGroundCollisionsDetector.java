package com.github.aadvorak.artilleryonline.battle.collision.detector.shell;

import com.github.aadvorak.artilleryonline.battle.calculations.BattleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.ShellCalculations;
import com.github.aadvorak.artilleryonline.battle.common.Collision;
import com.github.aadvorak.artilleryonline.battle.common.Contact;
import com.github.aadvorak.artilleryonline.battle.common.lines.Segment;
import com.github.aadvorak.artilleryonline.battle.utils.BattleUtils;
import org.springframework.stereotype.Component;

@Component
public class ShellGroundCollisionsDetector extends ShellCollisionsDetectorBase {

    protected Collision detectFirst(ShellCalculations shell, BattleCalculations battle) {
        var shellTrajectory = new Segment(shell.getPosition(), shell.getNext().getPosition());
        var firstPointUnderGround = BattleUtils.getFirstPointUnderGround(shellTrajectory, battle.getModel().getRoom());
        if (firstPointUnderGround != null) {
            var normal = shell.getPosition().vectorTo(firstPointUnderGround).normalized();
            return Collision.withGround(shell,
                    Contact.withUncheckedDepthOf(0.0, normal, firstPointUnderGround));
        }
        return null;
    }
}
