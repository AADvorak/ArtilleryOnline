package com.github.aadvorak.artilleryonline.battle.processor.vehicle.collisions;

import com.github.aadvorak.artilleryonline.battle.calculations.BattleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.VehicleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.WheelCalculations;
import com.github.aadvorak.artilleryonline.battle.common.Collision;
import com.github.aadvorak.artilleryonline.battle.common.VectorProjections;
import com.github.aadvorak.artilleryonline.battle.utils.BattleUtils;

public class VehicleGroundCollisionsProcessor {

    public static void process(VehicleCalculations vehicle, BattleCalculations battle) {
        calculateNextGroundPositions(vehicle, battle);
        var collision = VehicleGroundCollisionsDetector.detectFirst(vehicle, battle);
        if (collision != null) {
            resolve(collision, battle);
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

    private static void resolve(Collision collision, BattleCalculations battle) {
        recalculateVehicleVelocity(collision);
        collision.getPair().first().calculateNextPosition(battle.getModel().getCurrentTimeStepSecs());
        recalculateVehiclePosition(collision);
    }

    private static void recalculateVehicleVelocity(Collision collision) {
        var velocityProjections = VectorProjections.copyOf(collision.getVelocitiesProjections().first());
        velocityProjections.setNormal(-0.5 * velocityProjections.getNormal());
        collision.getPair().first().setVelocity(velocityProjections.recoverVelocity());
    }

    private static void recalculateVehiclePosition(Collision collision) {
        collision.getPair().first().applyNormalMoveToNextPosition(collision.getInterpenetration().depth(),
                collision.getInterpenetration().angle());
    }
}
