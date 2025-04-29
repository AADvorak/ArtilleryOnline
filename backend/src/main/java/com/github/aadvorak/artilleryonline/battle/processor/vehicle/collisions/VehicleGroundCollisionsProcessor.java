package com.github.aadvorak.artilleryonline.battle.processor.vehicle.collisions;

import com.github.aadvorak.artilleryonline.battle.calculations.BattleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.VehicleCalculations;
import com.github.aadvorak.artilleryonline.battle.utils.CollisionUtils;

public class VehicleGroundCollisionsProcessor {

    public static void process(VehicleCalculations vehicle, BattleCalculations battle) {
        var collision = VehicleGroundCollisionsDetector.detectStrongest(vehicle, battle);
        if (collision != null) {
            CollisionUtils.resolveGroundCollision(collision, battle);
            vehicle.getModel().setUpdated(true);
            vehicle.getCollisions().add(collision);
            vehicle.setHasCollisions(true);
        }
    }

    public static boolean checkResolved(VehicleCalculations vehicle, BattleCalculations battle) {
        var collision = VehicleGroundCollisionsDetector.detectFirst(vehicle, battle);
        return collision == null;
    }
}
