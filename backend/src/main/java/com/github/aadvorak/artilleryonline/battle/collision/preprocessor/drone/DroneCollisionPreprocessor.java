package com.github.aadvorak.artilleryonline.battle.collision.preprocessor.drone;

import com.github.aadvorak.artilleryonline.battle.calculations.BattleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.DroneCalculations;
import com.github.aadvorak.artilleryonline.battle.collision.CollideObjectType;
import com.github.aadvorak.artilleryonline.battle.collision.Collision;
import com.github.aadvorak.artilleryonline.battle.collision.preprocessor.CollisionPreprocessor;
import com.github.aadvorak.artilleryonline.battle.updates.BattleModelRemoved;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Optional;

@Component
public class DroneCollisionPreprocessor implements CollisionPreprocessor {

    private static final double REMOVE_DESTROYED_MIN_HEIGHT = 1.0;

    @Override
    public Boolean process(Collision collision, BattleCalculations battle) {
        var first = collision.getPair().first();
        if (first instanceof DroneCalculations drone) {
            return process(drone, collision, battle);
        }
        return null;
    }

    // todo refactor
    private boolean process(DroneCalculations drone, Collision collision, BattleCalculations battle) {
        if (drone.getModel().getState().isDestroyed() && drone.getHeight() < REMOVE_DESTROYED_MIN_HEIGHT) {
            battle.getModel().getUpdates().removeDrone(drone.getId());
            return false;
        }
        var removedDroneIds = Optional.ofNullable(battle.getModel().getUpdates().getRemoved())
                .map(BattleModelRemoved::getDroneIds)
                .orElse(new HashSet<>());
        if (!removedDroneIds.contains(drone.getId())
                && collision.getImpact() > drone.getModel().getSpecs().getMinCollisionDestroyImpact()) {
            drone.getModel().getState().setDestroyed(true);
            if (CollideObjectType.GROUND.equals(collision.getType())) {
                battle.getModel().getUpdates().removeDrone(drone.getId());
            }
        }
        drone.getModel().getUpdate().setUpdated();
        return true;
    }
}
