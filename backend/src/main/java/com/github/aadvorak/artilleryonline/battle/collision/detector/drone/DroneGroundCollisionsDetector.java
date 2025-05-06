package com.github.aadvorak.artilleryonline.battle.collision.detector.drone;

import com.github.aadvorak.artilleryonline.battle.calculations.BattleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.Calculations;
import com.github.aadvorak.artilleryonline.battle.calculations.DroneCalculations;
import com.github.aadvorak.artilleryonline.battle.collision.detector.CollisionsDetector;
import com.github.aadvorak.artilleryonline.battle.common.Collision;
import com.github.aadvorak.artilleryonline.battle.common.Contact;
import com.github.aadvorak.artilleryonline.battle.common.Position;
import com.github.aadvorak.artilleryonline.battle.common.lines.Circle;
import com.github.aadvorak.artilleryonline.battle.utils.GroundContactUtils;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class DroneGroundCollisionsDetector implements CollisionsDetector {

    @Override
    public Set<Collision> detect(Calculations<?> calculations, BattleCalculations battle, boolean first) {
        if (calculations instanceof DroneCalculations droneCalculations) {
            return detect(droneCalculations, battle, first);
        }
        return Set.of();
    }

    private Set<Collision> detect(DroneCalculations drone, BattleCalculations battle, boolean first) {
        var collisions = new HashSet<Collision>();
        var wallCollision = detectWallCollision(drone, battle);
        if (wallCollision != null) {
            collisions.add(wallCollision);
            if (first) return collisions;
        }
        var groundCollision = detectGroundCollision(drone, battle);
        if (groundCollision != null) {
            collisions.add(groundCollision);
        }
        return collisions;
    }

    private Collision detectGroundCollision(DroneCalculations drone, BattleCalculations battle) {
        var contact = GroundContactUtils.getGroundContact(
                new Circle(drone.getNext().getPosition().getCenter(), drone.getModel().getSpecs().getHullRadius()),
                battle.getModel().getRoom(), true);
        if (contact == null) {
            return null;
        }
        return Collision.withGround(drone, contact);
    }

    private Collision detectWallCollision(DroneCalculations drone, BattleCalculations battle) {
        var hullRadius = drone.getModel().getSpecs().getHullRadius();
        var xMax = battle.getModel().getRoom().getSpecs().getRightTop().getX();
        var xMin = battle.getModel().getRoom().getSpecs().getLeftBottom().getX();
        var nextPosition = drone.getNext().getPosition();
        var rightWallDepth = nextPosition.getX() + hullRadius - xMax;
        var rightWallContact = Contact.of(rightWallDepth, Math.PI / 2,
                new Position().setX(xMax).setY(nextPosition.getY()));
        if (rightWallContact != null) {
            return Collision.withWall(drone, rightWallContact);
        }
        var leftWallDepth = xMin - nextPosition.getX() + hullRadius;
        var leftWallContact = Contact.of(leftWallDepth, -Math.PI / 2,
                new Position().setX(xMin).setY(nextPosition.getY()));
        if (leftWallContact != null) {
            return Collision.withWall(drone, leftWallContact);
        }
        return null;
    }
}
