package com.github.aadvorak.artilleryonline.battle.processor.vehicle;

import com.github.aadvorak.artilleryonline.battle.calculations.BattleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.VehicleCalculations;
import com.github.aadvorak.artilleryonline.battle.events.VehicleCollideEvent;
import com.github.aadvorak.artilleryonline.battle.utils.VehicleUtils;

public class CollisionsProcessor {

    public static void process(BattleCalculations battle) {
        battle.getVehicles().forEach(vehicle -> {
            if (VehicleWallCollideProcessor.processCollide(vehicle, battle)) {
                vehicle.setHasCollisions(true);
                VehicleUtils.calculateNextPositionAndAngle(vehicle, battle);
            }
        });

        battle.getVehicles().forEach(vehicle -> {
            if (VehicleGroundCollideProcessor.processCollide(vehicle, battle)) {
                vehicle.setHasCollisions(true);
                VehicleUtils.calculateNextPositionAndAngle(vehicle, battle);
            }
        });

        battle.getVehicles().forEach(vehicle -> {
            if (VehicleCollideProcessor.processCollide(vehicle, battle)) {
                vehicle.setHasCollisions(true);
                VehicleUtils.calculateNextPositionAndAngle(vehicle, battle);
            }
        });

        battle.getVehicles().forEach(vehicle -> checkCollisionsResolved(vehicle, battle));
    }

    private static void checkCollisionsResolved(VehicleCalculations vehicle, BattleCalculations battle) {
        var resolved = VehicleWallCollideProcessor.checkResolved(vehicle, battle)
                && VehicleGroundCollideProcessor.checkResolved(vehicle, battle)
                && VehicleCollideProcessor.checkResolved(vehicle, battle);
        if (vehicle.isHasCollisions() && resolved) {
            vehicle.getCollisions().forEach(collideObject ->
                    battle.getModel().getEvents().addCollide(new VehicleCollideEvent()
                            .setVehicleId(vehicle.getModel().getId())
                            .setObject(collideObject)));
        }
        vehicle.setHasCollisions(!resolved);
    }
}
