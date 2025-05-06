package com.github.aadvorak.artilleryonline.battle.collision.postprocessor;

import com.github.aadvorak.artilleryonline.battle.calculations.BattleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.Calculations;

public interface CollisionPostprocessor {
    void process(Calculations<?> calculations, BattleCalculations battle);
}
