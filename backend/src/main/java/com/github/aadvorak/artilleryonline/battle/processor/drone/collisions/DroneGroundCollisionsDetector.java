package com.github.aadvorak.artilleryonline.battle.processor.drone.collisions;

import com.github.aadvorak.artilleryonline.battle.calculations.BattleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.DroneCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.NearestGroundPoint;
import com.github.aadvorak.artilleryonline.battle.calculator.wheel.GroundPositionCalculator;
import com.github.aadvorak.artilleryonline.battle.common.Collision;
import com.github.aadvorak.artilleryonline.battle.common.Contact;
import com.github.aadvorak.artilleryonline.battle.common.Position;

import java.util.Optional;

public class DroneGroundCollisionsDetector {

    public static Collision detectFirst(DroneCalculations drone, BattleCalculations battle) {
        var collision = detectWallCollision(drone, battle);
        if (collision != null) {
            return collision;
        }
        collision = detectGroundCollision(drone, battle);
        if (collision != null) {
            return collision;
        }
        return null;
    }

    private static Collision detectGroundCollision(DroneCalculations drone, BattleCalculations battle) {
        GroundPositionCalculator.calculateNext(drone, battle.getModel().getRoom());
        var depth = drone.getNext().getGroundDepth();
        var position = Optional.ofNullable(drone.getNext().getNearestGroundPoint())
                .map(NearestGroundPoint::position)
                .orElse(null);
        var contact = Contact.of(depth, drone.getNext().getGroundAngle(), position);
        if (contact == null) {
            return null;
        }
        return Collision.withGround(drone, contact);
    }

    private static Collision detectWallCollision(DroneCalculations drone, BattleCalculations battle) {
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
