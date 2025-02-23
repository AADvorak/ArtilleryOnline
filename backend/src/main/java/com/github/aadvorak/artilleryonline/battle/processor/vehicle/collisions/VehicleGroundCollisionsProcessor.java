package com.github.aadvorak.artilleryonline.battle.processor.vehicle.collisions;

import com.github.aadvorak.artilleryonline.battle.calculations.BattleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.VehicleCalculations;
import com.github.aadvorak.artilleryonline.battle.utils.BattleUtils;
import com.github.aadvorak.artilleryonline.battle.utils.CollisionUtils;

public class VehicleGroundCollisionsProcessor {

    public static void process(VehicleCalculations vehicle, BattleCalculations battle) {
        calculateNextGroundPositions(vehicle, battle);
        var collision = VehicleGroundCollisionsDetector.detectFirst(vehicle, battle);
        if (collision != null) {
            CollisionUtils.resolveGroundCollision(collision, battle);
            vehicle.getModel().setUpdated(true);
            vehicle.getCollisions().add(collision);
            vehicle.setHasCollisions(true);
        }
    }

    public static boolean checkResolved(VehicleCalculations vehicle, BattleCalculations battle) {
        calculateNextGroundPositions(vehicle, battle);
        var collision = VehicleGroundCollisionsDetector.detectFirst(vehicle, battle);
        return collision == null;
    }

    // todo is it needed?
    private static void calculateNextGroundPositions(VehicleCalculations vehicle, BattleCalculations battle) {
        vehicle.getRightWheel().getNext().setNearestGroundPointByX(BattleUtils.getNearestGroundPosition(
                vehicle.getRightWheel().getNext().getPosition().getX(),
                battle.getModel().getRoom()
        ));
        vehicle.getLeftWheel().getNext().setNearestGroundPointByX(BattleUtils.getNearestGroundPosition(
                vehicle.getLeftWheel().getNext().getPosition().getX(),
                battle.getModel().getRoom()
        ));
    }
}
