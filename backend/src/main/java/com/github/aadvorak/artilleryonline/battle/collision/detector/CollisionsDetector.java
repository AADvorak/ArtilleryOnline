package com.github.aadvorak.artilleryonline.battle.collision.detector;

import com.github.aadvorak.artilleryonline.battle.calculations.BattleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.Calculations;
import com.github.aadvorak.artilleryonline.battle.common.Collision;

import java.util.Set;

public interface CollisionsDetector {
    Set<Collision> detect(Calculations<?> calculations, BattleCalculations battle, boolean first);
}
