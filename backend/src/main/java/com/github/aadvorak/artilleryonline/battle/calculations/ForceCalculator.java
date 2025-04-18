package com.github.aadvorak.artilleryonline.battle.calculations;

import com.github.aadvorak.artilleryonline.battle.model.BattleModel;

public interface ForceCalculator<C extends BodyCalculations> {
    ForceAtPoint calculate(C calculations, BattleModel battleModel);
}
