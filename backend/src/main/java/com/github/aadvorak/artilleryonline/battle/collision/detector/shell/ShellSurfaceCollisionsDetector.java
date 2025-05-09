package com.github.aadvorak.artilleryonline.battle.collision.detector.shell;

import com.github.aadvorak.artilleryonline.battle.calculations.BattleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.ShellCalculations;
import com.github.aadvorak.artilleryonline.battle.common.Collision;
import com.github.aadvorak.artilleryonline.battle.common.Contact;
import com.github.aadvorak.artilleryonline.battle.common.lines.Segment;
import com.github.aadvorak.artilleryonline.battle.state.SurfaceState;
import com.github.aadvorak.artilleryonline.battle.utils.GeometryUtils;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class ShellSurfaceCollisionsDetector extends ShellCollisionDetectorBase {

    protected Set<Collision> detectFirst(ShellCalculations shell, BattleCalculations battle) {
        var surfaces = battle.getModel().getRoom().getState().getSurfaces();
        if (surfaces == null || surfaces.isEmpty()) {
            return Set.of();
        }
        for (var surface : surfaces) {
            var collision = detect(shell, surface);
            if (collision != null) {
                return Set.of(collision);
            }
        }
        return Set.of();
    }

    private Collision detect(ShellCalculations shell, SurfaceState surface) {
        var shellTrace = new Segment(shell.getPosition(), shell.getNext().getPosition());
        var vehicleBottom = new Segment(surface.getBegin(), surface.getEnd());
        var intersectionPoint = GeometryUtils.getSegmentsIntersectionPoint(shellTrace, vehicleBottom);
        if (intersectionPoint != null) {
            var projection = GeometryUtils.getPointToLineProjection(shell.getPosition(), vehicleBottom);
            var contact = Contact.withUncheckedDepthOf(0.0,
                    shell.getPosition().vectorTo(projection).normalized(), intersectionPoint);
            return Collision.withSurface(shell, contact);
        }
        return null;
    }
}
