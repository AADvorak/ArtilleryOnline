package com.github.aadvorak.artilleryonline.battle.processor.vehicle.collisions;

import com.github.aadvorak.artilleryonline.battle.calculations.BattleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.VehicleCalculations;
import com.github.aadvorak.artilleryonline.battle.events.VehicleCollideEvent;
import com.github.aadvorak.artilleryonline.dto.response.CollisionResponse;

public class CollisionsProcessor {

    public static void process(BattleCalculations battle, long additionalIterationsNumber) {
        processCollisions(battle, additionalIterationsNumber);
        if (collisionsExist(battle)) {
            checkCollisionsResolved(battle);
        }
    }

    private static void processCollisions(BattleCalculations battle, long additionalIterationsNumber) {
        processCollisionsStep(battle);
        var additionalIteration = 0;
        while (collisionsExist(battle) && additionalIteration < additionalIterationsNumber) {
            processCollisionsStep(battle);
            additionalIteration++;
        }
    }

    // todo may be, may be not
    private static void applyNextPositionsAndAngles(BattleCalculations battle) {
        battle.getVehicles().forEach(VehicleCalculations::applyNextPositionAndAngle);
    }

    private static void processCollisionsStep(BattleCalculations battle) {
        battle.getVehicles().forEach(vehicle -> vehicle.setHasCollisions(false));

        battle.getVehicles().forEach(vehicle ->
                VehicleGroundCollisionsProcessor.process(vehicle, battle));

        battle.getVehicles().forEach(vehicle ->
                VehicleCollisionsProcessor.process(vehicle, battle));
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
        var resolved = VehicleGroundCollisionsProcessor.checkResolved(vehicle, battle)
                && VehicleCollisionsProcessor.checkResolved(vehicle, battle);
        if (!vehicle.getCollisions().isEmpty() && resolved) {
            vehicle.getCollisions().forEach(collision ->
                    battle.getModel().getEvents().addCollide(new VehicleCollideEvent()
                            .setVehicleId(vehicle.getModel().getId())
                            .setObject(CollisionResponse.of(collision))));
        }
        vehicle.setHasCollisions(!resolved);
    }
}
