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
        CollisionUtils.recalculateVelocitiesRigid(collision);

        ((DroneCalculations) collision.getPair().first())
                .calculateNextPosition(battle.getModel().getCurrentTimeStepSecs());
        ((DroneCalculations) collision.getPair().second())
                .calculateNextPosition(battle.getModel().getCurrentTimeStepSecs());

        recalculateNextPositions(collision);
        collision.getPair().second().getCollisions().add(collision.inverted());
        ((DroneModel) collision.getPair().second().getModel()).getUpdate().setUpdated();
    }

    private static void recalculateNextPositions(Collision collision) {
        var drone = (DroneCalculations) collision.getPair().first();
        var otherDrone = (DroneCalculations) collision.getPair().second();
        var mass = collision.getPair().first().getMass();
        var otherMass = collision.getPair().second().getMass();
        var normalMovePerMass = collision.getInterpenetration().depth() / (mass + otherMass);
        var normalMove = normalMovePerMass * otherMass;
        var otherNormalMove = normalMovePerMass * mass;
        drone.applyNormalMoveToNextPosition(normalMove, collision.getInterpenetration().angle());
        otherDrone.applyNormalMoveToNextPosition(- otherNormalMove, collision.getInterpenetration().angle());
    }
}
