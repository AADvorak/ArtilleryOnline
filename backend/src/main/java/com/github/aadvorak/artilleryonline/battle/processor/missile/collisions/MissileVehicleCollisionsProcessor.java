package com.github.aadvorak.artilleryonline.battle.processor.missile.collisions;

import com.github.aadvorak.artilleryonline.battle.calculations.*;
import com.github.aadvorak.artilleryonline.battle.model.VehicleModel;
import com.github.aadvorak.artilleryonline.battle.processor.damage.DamageProcessor;
import com.github.aadvorak.artilleryonline.battle.utils.CollisionUtils;

public class MissileVehicleCollisionsProcessor {

    public static void process(MissileCalculations missile, BattleCalculations battle) {
        var collision = MissileVehicleCollisionsDetector.detectHead(missile, battle);
        if (collision != null) {
            var hitObject = collision.getPair().second();
            ((VehicleModel) hitObject.getModel()).setUpdated(true);
            missile.getCollisions().add(collision);
            if (hitObject instanceof VehicleCalculations vehicle) {
                DamageProcessor.processHitVehicle(vehicle, missile, battle);
            }
            if (hitObject instanceof WheelCalculations wheel) {
                DamageProcessor.processHitTrack(wheel.getVehicle(), missile, battle);
            }
            CollisionUtils.pushVehicleByDirectHit(collision);
        } else {
            collision = MissileVehicleCollisionsDetector.detectBody(missile, battle);
            if (collision != null) {
                missile.getCollisions().add(collision);
                DamageProcessor.processHit(missile, battle);
            }
        }
    }
}
