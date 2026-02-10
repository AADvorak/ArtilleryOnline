package com.github.aadvorak.artilleryonline.battle.collision.preprocessor.missile;

import com.github.aadvorak.artilleryonline.battle.calculations.*;
import com.github.aadvorak.artilleryonline.battle.collision.preprocessor.CollisionPreprocessor;
import com.github.aadvorak.artilleryonline.battle.collision.Collision;
import com.github.aadvorak.artilleryonline.battle.model.VehicleModel;
import com.github.aadvorak.artilleryonline.battle.processor.damage.DamageProcessor;
import com.github.aadvorak.artilleryonline.battle.processor.statistics.StatisticsProcessor;
import org.springframework.stereotype.Component;

@Component
public class MissileCollisionPreprocessor implements CollisionPreprocessor {

    @Override
    public Boolean process(Collision collision, BattleCalculations battle) {
        var first = collision.getPair().first();
        if (first instanceof MissileCalculations missile) {
            return process(missile, collision, battle);
        }
        return null;
    }

    private boolean process(MissileCalculations missile, Collision collision, BattleCalculations battle) {
        return switch (collision.getType()) {
            case DRONE -> processDrone(missile, collision, battle);
            case VEHICLE -> processVehicle(missile, collision, battle);
            case MISSILE -> processMissile(missile, collision, battle);
            case GROUND -> processGround(missile, collision, battle);
            default -> false;
        };
    }

    private boolean processDrone(MissileCalculations missile, Collision collision, BattleCalculations battle) {
        var drone = (DroneCalculations) collision.getPair().second();
        drone.getModel().getState().setDestroyed(true);
        battle.getModel().getUpdates().removeDrone(drone.getId());
        StatisticsProcessor.increaseDestroyedDrones(missile.getModel().getNickname(), battle);
        DamageProcessor.processHit(missile, collision, battle);
        return false;
    }

    private boolean processVehicle(MissileCalculations missile, Collision collision, BattleCalculations battle) {
        collision.setHit(true);
        var contactNormal = collision.getContact().normal();
        var missileNormal = missile.getPositions().getTail().vectorTo(missile.getPositions().getHead()).normalized();
        if (missileNormal.dotProduct(contactNormal) > 0.5) {
            var hitObject = collision.getPair().second();
            ((VehicleModel) hitObject.getModel()).getUpdate().setUpdated();
            if (hitObject instanceof VehicleCalculations vehicle) {
                DamageProcessor.processHitVehicle(vehicle, missile, collision, battle);
            }
            if (hitObject instanceof WheelCalculations wheel) {
                DamageProcessor.processHitTrack(wheel.getVehicle(), missile, collision, battle);
            }
        } else {
            DamageProcessor.processHit(missile, collision, battle);
        }
        return true;
    }

    private boolean processMissile(MissileCalculations missile, Collision collision, BattleCalculations battle) {
        var otherMissile = (MissileCalculations) collision.getPair().second();
        DamageProcessor.processHit(missile, collision, battle);
        DamageProcessor.processHit(otherMissile, collision, battle);
        return false;
    }

    private boolean processGround(MissileCalculations missile, Collision collision, BattleCalculations battle) {
        DamageProcessor.processHit(missile, collision, battle);
        return false;
    }
}
