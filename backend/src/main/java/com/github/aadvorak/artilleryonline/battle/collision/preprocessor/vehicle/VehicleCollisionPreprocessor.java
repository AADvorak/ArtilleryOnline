package com.github.aadvorak.artilleryonline.battle.collision.preprocessor.vehicle;

import com.github.aadvorak.artilleryonline.battle.calculations.BattleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.VehicleCalculations;
import com.github.aadvorak.artilleryonline.battle.collision.preprocessor.CollisionPreprocessor;
import com.github.aadvorak.artilleryonline.battle.common.Collision;
import com.github.aadvorak.artilleryonline.battle.model.BattleModel;
import com.github.aadvorak.artilleryonline.battle.model.VehicleModel;
import com.github.aadvorak.artilleryonline.battle.processor.damage.DamageProcessor;
import org.springframework.stereotype.Component;

@Component
public class VehicleCollisionPreprocessor implements CollisionPreprocessor {

    @Override
    public boolean process(Collision collision, BattleCalculations battle) {
        var first = collision.getPair().first();
        var second = collision.getPair().second();
        if (first instanceof VehicleCalculations firstVehicle) {
            firstVehicle.getModel().setUpdated(true);
        }
        if (second instanceof VehicleCalculations secondVehicle) {
            secondVehicle.getModel().setUpdated(true);
        }
        if (first instanceof VehicleCalculations firstVehicle && second instanceof VehicleCalculations secondVehicle) {
            calculateAndApplyDamage(collision, firstVehicle.getModel(), secondVehicle.getModel(), battle.getModel());
        }
        return true;
    }

    private void calculateAndApplyDamage(Collision collision, VehicleModel firstModel,
                                         VehicleModel secondModel, BattleModel battleModel) {
        calculateAndApplyDamage(collision, battleModel, firstModel, secondModel);
        calculateAndApplyDamage(collision, battleModel, secondModel, firstModel);
    }

    private void calculateAndApplyDamage(Collision collision, BattleModel battleModel,
                                                VehicleModel receiver, VehicleModel causer) {
        var minImpact = receiver.getSpecs().getMinCollisionDamageImpact();
        var impact = collision.getImpact();
        if (impact > minImpact) {
            var damage = receiver.getSpecs().getCollisionDamageCoefficient() * (impact - minImpact);
            DamageProcessor.applyDamageToVehicle(damage, receiver, battleModel, causer.getUserId());
        }
    }
}
