package com.github.aadvorak.artilleryonline.battle;

import com.github.aadvorak.artilleryonline.battle.processor.ActiveBattleStepProcessor;
import com.github.aadvorak.artilleryonline.battle.processor.WaitingBattleStepProcessor;
import com.github.aadvorak.artilleryonline.collection.BattleUpdatesQueue;
import com.github.aadvorak.artilleryonline.collection.UserBattleMap;
import com.github.aadvorak.artilleryonline.properties.ApplicationSettings;
import lombok.RequiredArgsConstructor;

import java.util.Set;

@RequiredArgsConstructor
public class BattleRunner implements Runnable {

    private final Battle battle;

    private final Set<String> userKeys;

    private final UserBattleMap userBattleMap;

    private final BattleUpdatesQueue battleUpdatesQueue;

    private final ApplicationSettings applicationSettings;

    private final WaitingBattleStepProcessor waitingBattleStepProcessor = new WaitingBattleStepProcessor();
    private final ActiveBattleStepProcessor activeBattleStepProcessor = new ActiveBattleStepProcessor();

    @Override
    public void run() {
        while (!BattleStage.FINISHED.equals(battle.getBattleStage())) {
            try {
                Thread.sleep(Battle.TIME_STEP_MS);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            processBattleStep();
            setBattleUpdatedByTimeout();
            sendBattleToUpdatesQueueIfUpdated();
        }
        removeBattleFromMap();
    }

    private void processBattleStep() {
        if (BattleStage.WAITING.equals(battle.getBattleStage())) {
            waitingBattleStepProcessor.processStep(battle);
        } else if (BattleStage.ACTIVE.equals(battle.getBattleStage())) {
            activeBattleStepProcessor.processStep(battle);
        }
    }

    private void removeBattleFromMap() {
        userKeys.forEach(userBattleMap::remove);
    }

    private void setBattleUpdatedByTimeout() {
        var battleModel = battle.getModel();
        if (!battleModel.isUpdated()
                && battle.getAbsoluteTime() - battleModel.getLastUpdateTime()
                > applicationSettings.getBattleUpdateTimeout()) {
            battleModel.setUpdated(true);
        }
    }

    private void sendBattleToUpdatesQueueIfUpdated() {
        if (!applicationSettings.isClientProcessing() || battle.getModel().isUpdated()) {
            battleUpdatesQueue.add(battle);
            battle.getModel().setUpdated(false);
        }
    }
}
