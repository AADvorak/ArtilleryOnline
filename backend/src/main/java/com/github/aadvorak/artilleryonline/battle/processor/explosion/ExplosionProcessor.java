package com.github.aadvorak.artilleryonline.battle.processor.explosion;

import com.github.aadvorak.artilleryonline.battle.calculations.BattleCalculations;
import com.github.aadvorak.artilleryonline.battle.model.BattleModel;
import com.github.aadvorak.artilleryonline.battle.model.ExplosionModel;
import com.github.aadvorak.artilleryonline.battle.processor.BeforeStep1Processor;
import org.springframework.stereotype.Component;

@Component
public class ExplosionProcessor implements BeforeStep1Processor {

    @Override
    public void process(BattleCalculations battle) {
        battle.getModel().getExplosions().values().forEach(explosionModel ->
                processExplosion(explosionModel, battle.getModel()));
    }

    private void processExplosion(ExplosionModel explosionModel, BattleModel battleModel) {
        var explosionState = explosionModel.getState();
        explosionState.setTime(explosionState.getTime() + battleModel.getCurrentTimeStepSecs());
        if (explosionState.getTime() > explosionModel.getSpecs().getDuration()) {
            battleModel.getUpdates().removeExplosion(explosionModel.getId());
            return;
        }
        explosionState.setRadius(explosionModel.getSpecs().getRadius()
                * (explosionModel.getSpecs().getDuration() - explosionState.getTime())
                / explosionModel.getSpecs().getDuration());
    }
}
