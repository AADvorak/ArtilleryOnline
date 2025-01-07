package com.github.aadvorak.artilleryonline.battle.processor.vehicle.collisions;

import com.github.aadvorak.artilleryonline.battle.calculations.BattleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.VehicleCalculations;
import com.github.aadvorak.artilleryonline.battle.events.VehicleCollideEvent;
import com.github.aadvorak.artilleryonline.battle.utils.VehicleUtils;
import com.github.aadvorak.artilleryonline.dto.response.CollisionResponse;

public class CollisionsProcessor {

    public static void process(BattleCalculations battle) {
        processCollisions(battle);
        if (collisionsExist(battle)) {
            checkCollisionsResolved(battle);
        }
    }

    private static void processCollisions(BattleCalculations battle) {
        battle.getVehicles().forEach(vehicle -> {
            if (VehicleWallCollisionsProcessor.process(vehicle, battle)) {
                vehicle.setHasCollisions(true);
                VehicleUtils.calculateNextPositionAndAngle(vehicle, battle);
            }
        });

        battle.getVehicles().forEach(vehicle -> {
            if (VehicleGroundCollisionsProcessor.process(vehicle, battle)) {
                vehicle.setHasCollisions(true);
                VehicleUtils.recalculateNextPositionAndAngle(vehicle, battle);
            }
        });

        battle.getVehicles().forEach(vehicle -> {
            if (VehicleCollisionsProcessor.process(vehicle, battle)) {
                vehicle.setHasCollisions(true);
                VehicleUtils.recalculateNextPositionAndAngle(vehicle, battle);
            }
        });
    }

    private static boolean collisionsExist(BattleCalculations battle) {
        for (var vehicle : battle.getVehicles()) {
            if (vehicle.isHasCollisions()) {
                return true;
            }
        }
        return false;
    }

    private static void checkCollisionsResolved(BattleCalculations battle) {
        battle.getVehicles().forEach(vehicle -> checkCollisionResolved(vehicle, battle));
    }

    private static void checkCollisionResolved(VehicleCalculations vehicle, BattleCalculations battle) {
        var resolved = VehicleWallCollisionsProcessor.checkResolved(vehicle, battle)
                && VehicleGroundCollisionsProcessor.checkResolved(vehicle, battle)
                && VehicleCollisionsProcessor.checkResolved(vehicle, battle);
        if (vehicle.isHasCollisions() && resolved) {
            vehicle.getCollisions().forEach(collision ->
                    battle.getModel().getEvents().addCollide(new VehicleCollideEvent()
                            .setVehicleId(vehicle.getModel().getId())
                            .setObject(CollisionResponse.of(collision))));
        }
        vehicle.setHasCollisions(!resolved);
    }
}
