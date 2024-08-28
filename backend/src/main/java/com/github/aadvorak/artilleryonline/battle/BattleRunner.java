package com.github.aadvorak.artilleryonline.battle;

import com.github.aadvorak.artilleryonline.battle.processor.ActiveBattleStepProcessor;
import com.github.aadvorak.artilleryonline.battle.processor.FinishedBattleStepProcessor;
import com.github.aadvorak.artilleryonline.battle.processor.WaitingBattleStepProcessor;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BattleRunner implements Runnable {

    private final Battle battle;

    private final WaitingBattleStepProcessor waitingBattleStepProcessor = new WaitingBattleStepProcessor();
    private final ActiveBattleStepProcessor activeBattleStepProcessor = new ActiveBattleStepProcessor();
    private final FinishedBattleStepProcessor finishedBattleStepProcessor = new FinishedBattleStepProcessor();

    @Override
    public void run() {
        while (!BattleStage.TERMINATE.equals(battle.getBattleStage())) {
            try {
                Thread.sleep(Battle.TIME_STEP);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            processBattleStep();
        }
    }

    private void processBattleStep() {
        if (BattleStage.WAITING.equals(battle.getBattleStage())) {
            waitingBattleStepProcessor.processStep(battle);
        } else if (BattleStage.ACTIVE.equals(battle.getBattleStage())) {
            activeBattleStepProcessor.processStep(battle);
        } else if (BattleStage.FINISHED.equals(battle.getBattleStage())) {
            finishedBattleStepProcessor.processStep(battle);
        }
    }
}
