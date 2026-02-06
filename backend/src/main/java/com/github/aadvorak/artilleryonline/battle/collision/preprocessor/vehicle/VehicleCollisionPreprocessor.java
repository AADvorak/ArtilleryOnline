package com.github.aadvorak.artilleryonline.battle.collision.preprocessor.vehicle;

import com.github.aadvorak.artilleryonline.battle.calculations.BattleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.VehicleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.WheelCalculations;
import com.github.aadvorak.artilleryonline.battle.collision.Collision;
import com.github.aadvorak.artilleryonline.battle.collision.preprocessor.CollisionPreprocessor;
import com.github.aadvorak.artilleryonline.battle.model.BattleModel;
import com.github.aadvorak.artilleryonline.battle.model.VehicleModel;
import com.github.aadvorak.artilleryonline.battle.processor.damage.DamageProcessor;
import com.github.aadvorak.artilleryonline.properties.ApplicationSettings;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VehicleCollisionPreprocessor implements CollisionPreprocessor {

    private final ApplicationSettings applicationSettings;

    @Override
    public Boolean process(Collision collision, BattleCalculations battle) {
        var first = collision.getPair().first();
        var second = collision.getPair().second();
        var firstModel = first instanceof VehicleCalculations || first instanceof WheelCalculations
                ? (VehicleModel) first.getModel() : null;
        var secondModel = second instanceof VehicleCalculations || second instanceof WheelCalculations
                ? (VehicleModel) second.getModel() : null;
        if (firstModel != null && !applicationSettings.isClientCollisionsProcessing()) {
            firstModel.getUpdate().setUpdated();
        }
        if (secondModel != null && !applicationSettings.isClientCollisionsProcessing()) {
            secondModel.getUpdate().setUpdated();
        }
        if (firstModel != null && secondModel != null) {
            calculateAndApplyDamage(collision, firstModel, secondModel, battle.getModel());
        }
        return null;
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
            DamageProcessor.applyDamageToVehicle(damage, receiver, battleModel, causer.getNickname());
        }
    }
}
