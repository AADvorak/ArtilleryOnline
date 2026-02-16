package com.github.aadvorak.artilleryonline.battle.processor.explosion;

import com.github.aadvorak.artilleryonline.battle.common.Position;
import com.github.aadvorak.artilleryonline.battle.model.BattleModel;
import com.github.aadvorak.artilleryonline.battle.model.ExplosionModel;
import com.github.aadvorak.artilleryonline.battle.specs.ExplosionSpecs;
import com.github.aadvorak.artilleryonline.battle.state.ExplosionState;

public class ExplosionInitializer {

    public static void init(Position position, double radius, BattleModel battleModel) {
        if (radius <= 0) {
            return;
        }
        var explosionSpecs = new ExplosionSpecs()
                .setDuration(0.4 * radius)
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
}
