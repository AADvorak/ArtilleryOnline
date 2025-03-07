package com.github.aadvorak.artilleryonline.battle.processor.drone.collisions;

import com.github.aadvorak.artilleryonline.battle.calculations.BattleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.DroneCalculations;
import com.github.aadvorak.artilleryonline.battle.utils.CollisionUtils;

public class DroneGroundCollisionsProcessor {

    public static void process(DroneCalculations drone, BattleCalculations battle) {
        var collision = DroneGroundCollisionsDetector.detectFirst(drone, battle);
        if (collision != null) {
            if (drone.getModel().getState().isDestroyed()) {
                battle.getModel().getUpdates().removeDrone(drone.getId());
            } else {
                CollisionUtils.resolveGroundCollision(collision, battle);
                drone.getModel().getUpdate().setUpdated();
                drone.getCollisions().add(collision);
            }
        }
    }
}
