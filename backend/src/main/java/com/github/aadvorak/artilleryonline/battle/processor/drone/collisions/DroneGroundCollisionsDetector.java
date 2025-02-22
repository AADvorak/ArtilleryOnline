package com.github.aadvorak.artilleryonline.battle.processor.drone.collisions;

import com.github.aadvorak.artilleryonline.battle.calculations.BattleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.DroneCalculations;
import com.github.aadvorak.artilleryonline.battle.calculator.wheel.GroundPositionCalculator;
import com.github.aadvorak.artilleryonline.battle.common.Collision;
import com.github.aadvorak.artilleryonline.battle.common.Interpenetration;

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
        var interpenetration = Interpenetration.of(depth, drone.getNext().getGroundAngle());
        if (interpenetration == null) {
            return null;
        }
        return Collision.withGround(drone, interpenetration);
    }

    private static Collision detectWallCollision(DroneCalculations drone, BattleCalculations battle) {
        var hullRadius = drone.getModel().getSpecs().getHullRadius();
        var xMax = battle.getModel().getRoom().getSpecs().getRightTop().getX();
        var xMin = battle.getModel().getRoom().getSpecs().getLeftBottom().getX();
        var nextPosition = drone.getNext().getPosition();
        var rightWallDepth = nextPosition.getX() + hullRadius - xMax;
        var rightWallInterpenetration = Interpenetration.of(rightWallDepth, Math.PI / 2);
        if (rightWallInterpenetration != null) {
            return Collision.withWall(drone, rightWallInterpenetration);
        }
        var leftWallDepth = xMin - nextPosition.getX() + hullRadius;
        var leftWallInterpenetration = Interpenetration.of(leftWallDepth, -Math.PI / 2);
        if (leftWallInterpenetration != null) {
            return Collision.withWall(drone, leftWallInterpenetration);
        }
        return null;
    }
}
