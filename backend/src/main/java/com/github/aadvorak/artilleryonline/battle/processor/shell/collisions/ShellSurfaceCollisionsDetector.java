package com.github.aadvorak.artilleryonline.battle.processor.shell.collisions;

import com.github.aadvorak.artilleryonline.battle.calculations.BattleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.ShellCalculations;
import com.github.aadvorak.artilleryonline.battle.common.Collision;
import com.github.aadvorak.artilleryonline.battle.common.Interpenetration;
import com.github.aadvorak.artilleryonline.battle.common.lines.Segment;
import com.github.aadvorak.artilleryonline.battle.state.SurfaceState;
import com.github.aadvorak.artilleryonline.battle.utils.GeometryUtils;

public class ShellSurfaceCollisionsDetector {

    public static Collision detectFirst(ShellCalculations shell, BattleCalculations battle) {
        var surfaces = battle.getModel().getRoom().getState().getSurfaces();
        if (surfaces == null || surfaces.isEmpty()) {
            return null;
        }
        for (var surface : surfaces) {
            var collision = detect(shell, surface);
            if (collision != null) {
                return collision;
            }
        }
        return null;
    }

    private static Collision detect(ShellCalculations shell, SurfaceState surface) {
        var shellTrace = new Segment(shell.getPosition(), shell.getNext().getPosition());
        var vehicleBottom = new Segment(surface.getBegin(), surface.getEnd());
        if (GeometryUtils.getSegmentsIntersectionPoint(shellTrace, vehicleBottom) != null) {
            var projection = GeometryUtils.getPointToLineProjection(shell.getPosition(), vehicleBottom);
            var interpenetration = Interpenetration.withUncheckedDepth(0.0, shell.getPosition(), projection);
            return Collision.withSurface(shell, interpenetration);
        }
        return null;
    }
}
