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
            var collision = detectWheelCollision(wheel, battle);
            if (collision != null) {
                collisions.add(collision);
                if (first) return collisions;
            }
        }
        return collisions;
    }

    private static Collision detectWheelCollision(WheelCalculations wheel, BattleCalculations battle) {
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
}
