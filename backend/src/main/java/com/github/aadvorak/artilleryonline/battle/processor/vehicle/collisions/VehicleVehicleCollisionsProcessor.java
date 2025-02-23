package com.github.aadvorak.artilleryonline.battle.processor.vehicle.collisions;

import com.github.aadvorak.artilleryonline.battle.calculations.BattleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.VehicleCalculations;
import com.github.aadvorak.artilleryonline.battle.common.Collision;
import com.github.aadvorak.artilleryonline.battle.model.BattleModel;
import com.github.aadvorak.artilleryonline.battle.model.VehicleModel;
import com.github.aadvorak.artilleryonline.battle.processor.damage.DamageProcessor;
import com.github.aadvorak.artilleryonline.battle.utils.CollisionUtils;

public class VehicleVehicleCollisionsProcessor {

    public static void process(VehicleCalculations vehicle, BattleCalculations battle) {
        var collision = VehicleVehicleCollisionsDetector.detectStrongest(vehicle, battle);
        if (collision != null) {
            vehicle.setHasCollisions(true);
            calculateAndApplyDamage(collision, battle.getModel());
            resolve(collision, battle);
            vehicle.getModel().setUpdated(true);
            vehicle.getCollisions().add(collision);
        }
    }

    public static boolean checkResolved(VehicleCalculations vehicle, BattleCalculations battle) {
        var collision = VehicleVehicleCollisionsDetector.detectFirst(vehicle, battle);
        return collision == null;
    }

    private static void resolve(Collision collision, BattleCalculations battle) {
        CollisionUtils.resolveRigidCollision(collision, battle);
        ((VehicleModel) collision.getPair().second().getModel()).setUpdated(true);
        collision.getPair().second().getVehicleCalculations().setHasCollisions(true);
    }

    private static void calculateAndApplyDamage(Collision collision, BattleModel battleModel) {
        var firstModel = (VehicleModel) collision.getPair().first().getModel();
        var secondModel = (VehicleModel) collision.getPair().second().getModel();
        calculateAndApplyDamage(collision, battleModel, firstModel, secondModel);
        calculateAndApplyDamage(collision, battleModel, secondModel, firstModel);
    }

    private static void calculateAndApplyDamage(Collision collision, BattleModel battleModel,
                                                VehicleModel receiver, VehicleModel causer) {
        var minImpact = receiver.getSpecs().getMinCollisionDamageImpact();
        var impact = collision.getImpact();
        if (impact > minImpact) {
            var damage = receiver.getSpecs().getCollisionDamageCoefficient() * (impact - minImpact);
            DamageProcessor.applyDamageToVehicle(damage, receiver, battleModel, causer.getUserId());
        }
    }
}
