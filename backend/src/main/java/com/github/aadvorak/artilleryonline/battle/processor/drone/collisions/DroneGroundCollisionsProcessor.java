package com.github.aadvorak.artilleryonline.battle.processor.drone.collisions;

import com.github.aadvorak.artilleryonline.battle.calculations.BattleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.DroneCalculations;
import com.github.aadvorak.artilleryonline.battle.common.Collision;
import com.github.aadvorak.artilleryonline.battle.common.VectorProjections;

public class DroneGroundCollisionsProcessor {

    public static void process(DroneCalculations drone, BattleCalculations battle) {
        var collision = DroneGroundCollisionsDetector.detectFirst(drone, battle);
        if (collision != null) {
            resolve(collision, battle);
            drone.getModel().getUpdate().setUpdated();
            drone.getCollisions().add(collision);
        }
    }

    private static void resolve(Collision collision, BattleCalculations battle) {
        recalculateVelocity(collision);
        ((DroneCalculations) collision.getPair().first())
                .calculateNextPosition(battle.getModel().getCurrentTimeStepSecs());
        recalculatePosition(collision);
    }

    private static void recalculateVelocity(Collision collision) {
        var velocityProjections = VectorProjections.copyOf(collision.getVelocitiesProjections().first());
        velocityProjections.setNormal(-0.5 * velocityProjections.getNormal());
        collision.getPair().first().setVelocity(velocityProjections.recoverVelocity());
    }

    private static void recalculatePosition(Collision collision) {
        ((DroneCalculations) collision.getPair().first())
                .applyNormalMoveToNextPosition(collision.getInterpenetration().depth(), collision.getInterpenetration().angle());
    }
}
