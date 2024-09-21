package com.github.aadvorak.artilleryonline.battle.processor.explosion;

import com.github.aadvorak.artilleryonline.battle.common.ShellType;
import com.github.aadvorak.artilleryonline.battle.model.BattleModel;
import com.github.aadvorak.artilleryonline.battle.model.ExplosionModel;
import com.github.aadvorak.artilleryonline.battle.model.ShellModel;
import com.github.aadvorak.artilleryonline.battle.specs.ExplosionSpecs;
import com.github.aadvorak.artilleryonline.battle.state.ExplosionState;

import java.util.List;

public class ExplosionProcessor {

    public static void initExplosion(ShellModel shellModel, BattleModel battleModel) {
        var explosionSpecs = new ExplosionSpecs()
                .setDuration(ShellType.HE.equals(shellModel.getSpecs().getType()) ? 0.4 : 0.2)
                .setRadius(shellModel.getSpecs().getRadius());
        var explosionState = new ExplosionState()
                .setTime(0.0)
                .setRadius(shellModel.getSpecs().getRadius())
                .setPosition(shellModel.getState().getPosition());
        var explosionModel = new ExplosionModel();
        explosionModel.setId(battleModel.getIdGenerator().generate());
        explosionModel.setSpecs(explosionSpecs);
        explosionModel.setState(explosionState);
        battleModel.getExplosions().put(explosionModel.getId(), explosionModel);
    }

    public static void processStep(ExplosionModel explosionModel, BattleModel battleModel, List<Integer> explosionIdsToRemove) {
        var explosionState = explosionModel.getState();
        explosionState.setTime(explosionState.getTime() + battleModel.getCurrentTimeStepSecs());
        if (explosionState.getTime() > explosionModel.getSpecs().getDuration()) {
            explosionIdsToRemove.add(explosionModel.getId());
            return;
        }
        explosionState.setRadius(explosionModel.getSpecs().getRadius()
                * (explosionModel.getSpecs().getDuration() - explosionState.getTime())
                / explosionModel.getSpecs().getDuration());
    }
}
