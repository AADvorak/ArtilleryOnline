package com.github.aadvorak.artilleryonline.battle.processor.vehicle.collisions;

import com.github.aadvorak.artilleryonline.battle.calculations.BattleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.VehicleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.WheelCalculations;
import com.github.aadvorak.artilleryonline.battle.calculator.wheel.GroundPositionCalculator;
import com.github.aadvorak.artilleryonline.battle.common.Collision;
import com.github.aadvorak.artilleryonline.battle.common.Interpenetration;

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
        return collisions;
    }

    private static Collision detectWheelGroundCollision(WheelCalculations wheel, BattleCalculations battle) {
        GroundPositionCalculator.calculateNext(wheel, battle.getModel().getRoom());
        var groundMaxDepth = battle.getModel().getRoom().getSpecs().getGroundMaxDepth();
        var depth = wheel.getNext().getGroundDepth() - groundMaxDepth;
        var interpenetration = Interpenetration.of(depth, wheel.getGroundAngle());
        if (interpenetration == null) {
            return null;
        }
        return Collision.withGround(wheel, interpenetration);
    }

    private static Collision detectWheelWallCollision(WheelCalculations wheel, BattleCalculations battle) {
        var wheelRadius = wheel.getVehicle().getModel().getSpecs().getWheelRadius();
        var xMax = battle.getModel().getRoom().getSpecs().getRightTop().getX();
        var xMin = battle.getModel().getRoom().getSpecs().getLeftBottom().getX();
        var nextPosition = wheel.getNext().getPosition();
        var rightWallDepth = nextPosition.getX() + wheelRadius - xMax;
        var rightWallInterpenetration = Interpenetration.of(rightWallDepth, Math.PI / 2);
        if (rightWallInterpenetration != null) {
            return Collision.withWall(wheel, rightWallInterpenetration);
        }
        var leftWallDepth = xMin - nextPosition.getX() + wheelRadius;
        var leftWallInterpenetration = Interpenetration.of(leftWallDepth, -Math.PI / 2);
        if (leftWallInterpenetration != null) {
            return Collision.withWall(wheel, leftWallInterpenetration);
        }
        return null;
    }
}
