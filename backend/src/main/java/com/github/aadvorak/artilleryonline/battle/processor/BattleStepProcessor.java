package com.github.aadvorak.artilleryonline.battle.processor;

import com.github.aadvorak.artilleryonline.battle.Battle;

public interface BattleStepProcessor {
    void processStep(Battle battle);
    void processError(Battle battle);
}
