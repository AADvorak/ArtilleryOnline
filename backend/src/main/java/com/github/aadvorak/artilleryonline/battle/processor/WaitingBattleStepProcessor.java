package com.github.aadvorak.artilleryonline.battle.processor;

import com.github.aadvorak.artilleryonline.collection.BattleTrackingMap;
import org.springframework.stereotype.Component;

@Component
public class WaitingBattleStepProcessor extends BattleStepProcessorBase implements BattleStepProcessor {
    public WaitingBattleStepProcessor(BattleTrackingMap battleTrackingMap) {
        super(battleTrackingMap);
    }
}
