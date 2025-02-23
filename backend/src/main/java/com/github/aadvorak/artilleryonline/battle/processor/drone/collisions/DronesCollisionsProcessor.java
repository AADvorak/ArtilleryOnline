package com.github.aadvorak.artilleryonline.battle.processor.drone.collisions;

import com.github.aadvorak.artilleryonline.battle.calculations.BattleCalculations;

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
    }
}
