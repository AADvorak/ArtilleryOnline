package com.github.aadvorak.artilleryonline.battle.calculations;

import com.github.aadvorak.artilleryonline.battle.model.BattleModel;

import java.util.List;

public interface CommonForceCalculator {
    List<BodyForce> calculate(BodyCalculations<?,?,?,?,?> calculations, BattleModel battleModel);
}
