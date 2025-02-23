package com.github.aadvorak.artilleryonline.battle.processor.drone.collisions;

import com.github.aadvorak.artilleryonline.battle.calculations.BattleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.DroneCalculations;
import com.github.aadvorak.artilleryonline.battle.common.Collision;
import com.github.aadvorak.artilleryonline.battle.model.DroneModel;
import com.github.aadvorak.artilleryonline.battle.utils.CollisionUtils;

public class DroneDroneCollisionsProcessor {

    public static void process(DroneCalculations drone, BattleCalculations battle) {
        var collision = DroneDroneCollisionsDetector.detectFirst(drone, battle);
        if (collision != null) {
            resolve(collision, battle);
            drone.getModel().getUpdate().setUpdated();
            drone.getCollisions().add(collision);
        }
    }

    private static void resolve(Collision collision, BattleCalculations battle) {
        CollisionUtils.resolveRigidCollision(collision, battle);
        ((DroneModel) collision.getPair().second().getModel()).getUpdate().setUpdated();
    }
}
