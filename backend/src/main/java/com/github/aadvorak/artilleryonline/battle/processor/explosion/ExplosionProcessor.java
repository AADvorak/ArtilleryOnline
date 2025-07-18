package com.github.aadvorak.artilleryonline.battle.processor.explosion;

import com.github.aadvorak.artilleryonline.battle.calculations.BattleCalculations;
import com.github.aadvorak.artilleryonline.battle.common.Position;
import com.github.aadvorak.artilleryonline.battle.model.BattleModel;
import com.github.aadvorak.artilleryonline.battle.model.ExplosionModel;
import com.github.aadvorak.artilleryonline.battle.processor.BeforeStep1Processor;
import com.github.aadvorak.artilleryonline.battle.specs.ExplosionSpecs;
import com.github.aadvorak.artilleryonline.battle.state.ExplosionState;
import org.springframework.stereotype.Component;

@Component
public class ExplosionProcessor implements BeforeStep1Processor {

    // todo move to separate class
    public static void initExplosion(Position position, double radius, BattleModel battleModel) {
        if (radius <= 0) {
            return;
        }
        var explosionSpecs = new ExplosionSpecs()
                .setDuration(0.8 * radius)
                .setRadius(radius);
        var explosionState = new ExplosionState()
                .setTime(0.0)
                .setRadius(radius)
                .setPosition(position);
        var explosionModel = new ExplosionModel();
        explosionModel.setId(battleModel.getIdGenerator().generate());
        explosionModel.setSpecs(explosionSpecs);
        explosionModel.setState(explosionState);
        battleModel.getExplosions().put(explosionModel.getId(), explosionModel);
        battleModel.getUpdates().addExplosion(explosionModel);
    }

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
