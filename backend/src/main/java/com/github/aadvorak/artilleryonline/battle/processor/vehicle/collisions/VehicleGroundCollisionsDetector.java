package com.github.aadvorak.artilleryonline.battle.processor.vehicle.collisions;

import com.github.aadvorak.artilleryonline.battle.calculations.BattleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.VehicleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.WheelCalculations;
import com.github.aadvorak.artilleryonline.battle.calculator.wheel.GroundPositionCalculator;
import com.github.aadvorak.artilleryonline.battle.common.Collision;

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
        if (wheel.getNext().getNearestGroundPointByX().getY() < wheel.getNext().getPosition().getY()) {
            return null;
        }
        var nearestGroundPoint = GroundPositionCalculator.getNearestGroundPoint(
                wheel.getNext().getPosition(),
                wheel.getVehicle().getModel().getSpecs().getWheelRadius(),
                battle.getModel().getRoom(),
                wheel.getSign().getValue()
        );
        var groundAngle = wheel.getGroundAngle();
        if (nearestGroundPoint != null) {
            return Collision.withGround(wheel, nearestGroundPoint.distance(), groundAngle);
        } else {
            var interpenetration = wheel.getNext().getNearestGroundPointByX().getY()
                    - wheel.getNext().getPosition().getY();
            return Collision.withGround(wheel, interpenetration, groundAngle);
        }
    }

    private static Collision detectWheelWallCollision(WheelCalculations wheel, BattleCalculations battle) {
        var wheelRadius = wheel.getVehicle().getModel().getSpecs().getWheelRadius();
        var xMax = battle.getModel().getRoom().getSpecs().getRightTop().getX();
        var xMin = battle.getModel().getRoom().getSpecs().getLeftBottom().getX();
        var nextPosition = wheel.getNext().getPosition();
        var rightWallInterpenetration = nextPosition.getX() + wheelRadius - xMax;
        if (rightWallInterpenetration > 0) {
            return Collision.withWall(wheel, rightWallInterpenetration, Math.PI / 2);
        }
        var leftWallInterpenetration = xMin - nextPosition.getX() + wheelRadius;
        if (leftWallInterpenetration > 0) {
            return Collision.withWall(wheel, leftWallInterpenetration, -Math.PI / 2);
        }
        return null;
    }
}
