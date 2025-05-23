package com.github.aadvorak.artilleryonline.battle.collision.preprocessor;

import com.github.aadvorak.artilleryonline.battle.calculations.BattleCalculations;
import com.github.aadvorak.artilleryonline.battle.collision.Collision;

public interface CollisionPreprocessor {
    /**
     * @return collision should be resolved or not
     */
    boolean process(Collision collision, BattleCalculations battle);
}
