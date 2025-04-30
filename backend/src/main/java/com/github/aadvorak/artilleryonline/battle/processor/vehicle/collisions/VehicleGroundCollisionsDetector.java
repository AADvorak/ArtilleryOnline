package com.github.aadvorak.artilleryonline.battle.processor.vehicle.collisions;

import com.github.aadvorak.artilleryonline.battle.calculations.BattleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.VehicleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.WheelCalculations;
import com.github.aadvorak.artilleryonline.battle.common.Collision;
import com.github.aadvorak.artilleryonline.battle.common.Contact;
import com.github.aadvorak.artilleryonline.battle.common.Position;
import com.github.aadvorak.artilleryonline.battle.common.lines.Circle;
import com.github.aadvorak.artilleryonline.battle.utils.CollisionUtils;
import com.github.aadvorak.artilleryonline.battle.utils.GroundContactUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class VehicleGroundCollisionsDetector {

    public static Collision detectFirst(VehicleCalculations vehicle, BattleCalculations battle) {
        var collisions = detect(vehicle, battle, true);
        if (collisions.isEmpty()) {
            return null;
        }
        return collisions.iterator().next();
    }

    public static Collision detectStrongest(VehicleCalculations vehicle, BattleCalculations battle) {
        var collisions = detect(vehicle, battle, false);
        return CollisionUtils.findStrongestCollision(collisions);
    }

    public static Set<Collision> detect(VehicleCalculations vehicle, BattleCalculations battle, boolean first) {
        Set<Collision> collisions = new HashSet<>();
        for (var wheel : List.of(vehicle.getRightWheel(), vehicle.getLeftWheel())) {
            var groundCollision = detectWheelGroundCollision(wheel, battle);
            if (groundCollision != null) {
                collisions.add(groundCollision);
                if (first) return collisions;
            }
            var wallCollision = detectWheelWallCollision(wheel, battle);
            if (wallCollision != null) {
                collisions.add(wallCollision);
                if (first) return collisions;
            }
        }
        var hullGroundCollision = detectHullGroundCollision(vehicle, battle);
        if (hullGroundCollision != null) {
            collisions.add(hullGroundCollision);
        }
        return collisions;
    }

    private static Collision detectWheelGroundCollision(WheelCalculations wheel, BattleCalculations battle) {
        var contact = GroundContactUtils.getGroundContact(
                new Circle(wheel.getNext().getPosition(), wheel.getModel().getSpecs().getWheelRadius()),
                battle.getModel().getRoom(), true);
        if (contact == null) {
            return null;
        }
        return Collision.withGround(wheel, contact);
    }

    private static Collision detectHullGroundCollision(VehicleCalculations vehicle, BattleCalculations battle) {
        var position = vehicle.getGeometryNextPosition();
        var contact = GroundContactUtils.getGroundContact(
                new Circle(position.getCenter(), vehicle.getModel().getSpecs().getRadius()),
                battle.getModel().getRoom(), true);
        if (contact == null) {
            return null;
        }
        var hullVector = position.getCenter().vectorTo(position.getCenter()
                .shifted(1.0, position.getAngle() + Math.PI / 2));
        if (hullVector.dotProduct(contact.normal()) < 0) {
            return null;
        }
        return Collision.withGround(vehicle, contact);
    }

    private static Collision detectWheelWallCollision(WheelCalculations wheel, BattleCalculations battle) {
        var wheelRadius = wheel.getVehicle().getModel().getSpecs().getWheelRadius();
        var xMax = battle.getModel().getRoom().getSpecs().getRightTop().getX();
        var xMin = battle.getModel().getRoom().getSpecs().getLeftBottom().getX();
        var nextPosition = wheel.getNext().getPosition();
        var rightWallDepth = nextPosition.getX() + wheelRadius - xMax;
        var rightWallContact = Contact.of(rightWallDepth, Math.PI / 2,
                new Position().setX(xMax).setY(nextPosition.getY()));
        if (rightWallContact != null) {
            return Collision.withWall(wheel, rightWallContact);
        }
        var leftWallDepth = xMin - nextPosition.getX() + wheelRadius;
        var leftWallContact = Contact.of(leftWallDepth, -Math.PI / 2,
                new Position().setX(xMin).setY(nextPosition.getY()));
        if (leftWallContact != null) {
            return Collision.withWall(wheel, leftWallContact);
        }
        return null;
    }
}
