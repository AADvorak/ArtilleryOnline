package com.github.aadvorak.artilleryonline.battle.processor.missile.collisions;

import com.github.aadvorak.artilleryonline.battle.calculations.BattleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.MissileCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.VehicleCalculations;
import com.github.aadvorak.artilleryonline.battle.common.Collision;
import com.github.aadvorak.artilleryonline.battle.common.Position;
import com.github.aadvorak.artilleryonline.battle.utils.CollisionUtils;

import java.util.Set;
import java.util.stream.Collectors;

public class MissileVehicleCollisionsDetector {

    public static Collision detectHead(MissileCalculations missile, BattleCalculations battle) {
        for (var vehicle : getVehicles(missile, battle)) {
            var collision = detectHead(missile, vehicle);
            if (collision != null) {
                return collision;
            }
        }
        return null;
    }

    public static Collision detectBody(MissileCalculations missile, BattleCalculations battle) {
        for (var vehicle : getVehicles(missile, battle)) {
            var collision = detectBody(missile, vehicle);
            if (collision != null) {
                return collision;
            }
        }
        return null;
    }

    public static Collision detectBody(MissileCalculations missile, VehicleCalculations vehicle) {
        var collision = detect(missile, missile.getPositions().getCenter(),
                missile.getNext().getPositions().getCenter(), vehicle);
        if (collision != null) {
            return collision;
        }
        return detect(missile, missile.getPositions().getTail(),
                missile.getNext().getPositions().getTail(), vehicle);
    }

    private static Collision detectHead(MissileCalculations missile, VehicleCalculations vehicle) {
        var position = missile.getPositions().getHead();
        var nextPosition = missile.getNext().getPositions().getHead();
        return detect(missile, position, nextPosition, vehicle);
    }

    private static Collision detect(MissileCalculations missile, Position position, Position nextPosition,
                                    VehicleCalculations vehicle) {
        var collision = CollisionUtils.detectWithVehicle(missile, position, nextPosition, vehicle);
        if (collision != null) {
            return collision;
        }
        collision = CollisionUtils.detectWithWheel(missile, position, nextPosition, vehicle.getRightWheel());
        if (collision != null) {
            return collision;
        }
        return CollisionUtils.detectWithWheel(missile, position, nextPosition, vehicle.getLeftWheel());
    }

    private static Set<VehicleCalculations> getVehicles(MissileCalculations missile, BattleCalculations battle) {
        return battle.getVehicles().stream()
                .filter(vehicleCalculations ->
                        !vehicleCalculations.getId().equals(missile.getModel().getVehicleId()))
                .collect(Collectors.toSet());
    }
}
