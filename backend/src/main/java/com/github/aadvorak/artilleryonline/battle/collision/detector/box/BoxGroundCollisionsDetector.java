package com.github.aadvorak.artilleryonline.battle.collision.detector.box;

import com.github.aadvorak.artilleryonline.battle.calculations.BattleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.BoxCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.Calculations;
import com.github.aadvorak.artilleryonline.battle.collision.Collision;
import com.github.aadvorak.artilleryonline.battle.collision.detector.CollisionsDetector;
import com.github.aadvorak.artilleryonline.battle.common.Contact;
import com.github.aadvorak.artilleryonline.battle.common.Position;
import com.github.aadvorak.artilleryonline.battle.common.lines.BodyPart;
import com.github.aadvorak.artilleryonline.battle.common.lines.Trapeze;
import com.github.aadvorak.artilleryonline.battle.utils.GroundContactUtils;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class BoxGroundCollisionsDetector implements CollisionsDetector {

    @Override
    public Set<Collision> detect(Calculations<?> calculations, BattleCalculations battle, boolean first) {
        if (calculations instanceof BoxCalculations boxCalculations) {
            return detect(boxCalculations, battle, first);
        }
        return Set.of();
    }

    private Set<Collision> detect(BoxCalculations box, BattleCalculations battle, boolean first) {
        var collisions = new HashSet<Collision>();
        var wallCollision = detectWallCollision(box, battle);
        if (wallCollision != null) {
            collisions.add(wallCollision);
            if (first) return collisions;
        }
        collisions.addAll(detectGroundCollisions(box, battle));
        return collisions;
    }

    private Set<Collision> detectGroundCollisions(BoxCalculations box, BattleCalculations battle) {
        return GroundContactUtils.getGroundContacts(
                BodyPart.of(box.getGeometryNextPosition(), box.getModel().getSpecs().getShape()),
                battle.getModel().getRoom(), true).stream()
                .map(contact -> Collision.withGround(box, contact))
                .collect(Collectors.toSet());
    }

    private Collision detectWallCollision(BoxCalculations box, BattleCalculations battle) {
        var nextX = box.getNextPosition().getX();
        var maxRadius = box.getModel().getPreCalc().getMaxRadius();
        var xMax = battle.getModel().getRoom().getSpecs().getRightTop().getX();
        var xMin = battle.getModel().getRoom().getSpecs().getLeftBottom().getX();
        if (nextX + maxRadius < xMax && nextX - maxRadius > xMin) {
            return null;
        }

        var trapeze = (Trapeze) BodyPart.of(box.getGeometryNextPosition(), box.getModel().getSpecs().getShape());
        var edges = Set.of(trapeze.bottomLeft(), trapeze.bottomRight(), trapeze.topRight(), trapeze.topLeft());
        var rightWallDepth = edges.stream()
                .map(edge -> new PointAndDepth(edge, edge.getX() - xMax))
                .max(DEPTH_DESCENDING_COMPARATOR)
                .orElse(new PointAndDepth(new Position(), 0.0));
        var rightWallContact = Contact.of(rightWallDepth.depth, Math.PI / 2, rightWallDepth.point);
        if (rightWallContact != null) {
            return Collision.withWall(box, rightWallContact);
        }
        var leftWallDepth = edges.stream()
                .map(edge -> new PointAndDepth(edge, xMin - edge.getX()))
                .max(DEPTH_DESCENDING_COMPARATOR)
                .orElse(new PointAndDepth(new Position(), 0.0));
        var leftWallContact = Contact.of(leftWallDepth.depth, -Math.PI / 2, leftWallDepth.point);
        if (leftWallContact != null) {
            return Collision.withWall(box, leftWallContact);
        }
        return null;
    }

    private record PointAndDepth(Position point, double depth) {
    }

    private static final Comparator<PointAndDepth> DEPTH_DESCENDING_COMPARATOR =
            Comparator.comparingDouble(PointAndDepth::depth);
}
