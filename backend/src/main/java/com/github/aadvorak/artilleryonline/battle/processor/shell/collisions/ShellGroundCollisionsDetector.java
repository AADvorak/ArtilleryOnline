package com.github.aadvorak.artilleryonline.battle.processor.shell.collisions;

import com.github.aadvorak.artilleryonline.battle.calculations.BattleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.ShellCalculations;
import com.github.aadvorak.artilleryonline.battle.common.Collision;
import com.github.aadvorak.artilleryonline.battle.common.lines.Segment;
import com.github.aadvorak.artilleryonline.battle.utils.BattleUtils;

public class ShellGroundCollisionsDetector {

    public static Collision detectFirst(ShellCalculations shell, BattleCalculations battle) {
        var shellTrajectory = new Segment(shell.getPosition(), shell.getNext().getPosition());
        var firstPointUnderGround = BattleUtils.getFirstPointUnderGround(shellTrajectory, battle.getModel().getRoom());
        if (firstPointUnderGround != null) {
            shell.getNext().setPosition(firstPointUnderGround);
            return Collision.ofShellWithGround(shell);
        }
        return null;
    }
}
