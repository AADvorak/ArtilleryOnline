package com.github.aadvorak.artilleryonline.battle.processor.drone.collisions;

import com.github.aadvorak.artilleryonline.battle.calculations.BattleCalculations;
import com.github.aadvorak.artilleryonline.battle.updates.BattleModelRemoved;

import java.util.HashSet;
import java.util.Optional;

public class DronesCollisionsProcessor {

    public static void process(BattleCalculations battle) {
        battle.getDrones().forEach(drone -> DroneGroundCollisionsProcessor.process(drone, battle));

        battle.getDrones().forEach(drone -> {
            if (drone.getCollisions().isEmpty()) {
                DroneDroneCollisionsProcessor.process(drone, battle);
            }
        });

        battle.getDrones().forEach(drone -> {
            if (drone.getCollisions().isEmpty()) {
                DroneVehicleCollisionsProcessor.process(drone, battle);
            }
        });

        battle.getDrones().forEach(drone -> {
            var removedDroneIds = Optional.ofNullable(battle.getModel().getUpdates().getRemoved())
                    .map(BattleModelRemoved::getDroneIds)
                    .orElse(new HashSet<>());
            if (!drone.getCollisions().isEmpty()
                    && !removedDroneIds.contains(drone.getId())) {
                var collision = drone.getCollisions().iterator().next();
                if (collision.getImpact() > drone.getModel().getSpecs().getMinCollisionDestroyImpact()) {
                    drone.getModel().getState().setDestroyed(true);
                    battle.getModel().getUpdates().removeDrone(drone.getId());
                }
            }
        });
    }
}
