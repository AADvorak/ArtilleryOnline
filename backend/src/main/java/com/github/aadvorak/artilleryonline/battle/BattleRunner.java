package com.github.aadvorak.artilleryonline.battle;

import com.github.aadvorak.artilleryonline.battle.processor.ActiveBattleStepProcessor;
import com.github.aadvorak.artilleryonline.battle.processor.FinishedBattleStepProcessor;
import com.github.aadvorak.artilleryonline.battle.processor.WaitingBattleStepProcessor;
import com.github.aadvorak.artilleryonline.collection.BattleUpdatesQueue;
import com.github.aadvorak.artilleryonline.collection.UserBattleMap;
import lombok.RequiredArgsConstructor;

import java.util.Set;

@RequiredArgsConstructor
public class BattleRunner implements Runnable {

    private final Battle battle;

    private final Set<String> userKeys;

    private final UserBattleMap userBattleMap;

    private final BattleUpdatesQueue battleUpdatesQueue;

    private final WaitingBattleStepProcessor waitingBattleStepProcessor = new WaitingBattleStepProcessor();
    private final ActiveBattleStepProcessor activeBattleStepProcessor = new ActiveBattleStepProcessor();
    private final FinishedBattleStepProcessor finishedBattleStepProcessor = new FinishedBattleStepProcessor();

    @Override
    public void run() {
        while (!BattleStage.TERMINATE.equals(battle.getBattleStage())) {
            try {
                Thread.sleep(Battle.TIME_STEP_MS);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            processBattleStep();
            sendBattleToUpdatesQueueIfUpdated();
        }
        removeBattleFromMap();
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

    private void removeBattleFromMap() {
        userKeys.forEach(userBattleMap::remove);
    }

    private void sendBattleToUpdatesQueueIfUpdated () {
        if (true) {
            battleUpdatesQueue.add(battle);
            battle.getModel().setUpdated(false);
        }
    }
}
