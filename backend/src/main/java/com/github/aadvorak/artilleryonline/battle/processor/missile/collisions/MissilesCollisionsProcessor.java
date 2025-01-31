package com.github.aadvorak.artilleryonline.battle.processor.missile.collisions;

import com.github.aadvorak.artilleryonline.battle.calculations.BattleCalculations;

public class MissilesCollisionsProcessor {

    public static void process(BattleCalculations battle) {
        battle.getMissiles().forEach(missile -> MissileGroundCollisionsProcessor.process(missile, battle));

        battle.getMissiles().forEach(missile -> {
            if (!missile.getCollisions().isEmpty()) {
                battle.getModel().getUpdates().removeMissile(missile.getId());
            }
        });
    }
}
