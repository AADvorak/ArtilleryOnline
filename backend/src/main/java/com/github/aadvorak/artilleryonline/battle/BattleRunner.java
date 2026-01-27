package com.github.aadvorak.artilleryonline.battle;

import com.github.aadvorak.artilleryonline.battle.events.BattleModelEvents;
import com.github.aadvorak.artilleryonline.battle.model.BattleModel;
import com.github.aadvorak.artilleryonline.battle.processor.ActiveBattleStepProcessor;
import com.github.aadvorak.artilleryonline.battle.processor.BattleUpdatesProcessor;
import com.github.aadvorak.artilleryonline.battle.processor.WaitingBattleStepProcessor;
import com.github.aadvorak.artilleryonline.battle.updates.BattleModelUpdates;
import com.github.aadvorak.artilleryonline.collection.UserBattleMap;
import com.github.aadvorak.artilleryonline.dto.response.BattleResponse;
import com.github.aadvorak.artilleryonline.entity.User;
import com.github.aadvorak.artilleryonline.model.Locale;
import com.github.aadvorak.artilleryonline.model.LocaleCode;
import com.github.aadvorak.artilleryonline.model.MessageSpecial;
import com.github.aadvorak.artilleryonline.model.UserBattleResult;
import com.github.aadvorak.artilleryonline.service.BattleHistoryService;
import com.github.aadvorak.artilleryonline.service.MessageService;
import com.github.aadvorak.artilleryonline.ws.BattleAggregatedSender;
import com.github.aadvorak.artilleryonline.ws.BattleUpdatesSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class BattleRunner {

    private final UserBattleMap userBattleMap;

    private final BattleUpdatesSender battleUpdatesSender;

    private final BattleAggregatedSender battleAggregatedSender;

    private final MessageService messageService;

    private final BattleHistoryService battleHistoryService;

    private final WaitingBattleStepProcessor waitingBattleStepProcessor;

    private final ActiveBattleStepProcessor activeBattleStepProcessor;

    private final BattleUpdatesProcessor battleUpdatesProcessor;

    @Async("runBattleExecutor")
    public void runBattle(Battle battle) {
        startUpdatesSender(battle);
        try {
            while (!BattleStage.FINISHED.equals(battle.getBattleStage())
                    && (!battle.getActiveUserIds().isEmpty() || !battle.getBotsVehicleIds().isEmpty())) {
                try {
                    Thread.sleep(Battle.TIME_STEP_MS);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                battle.getModel().setUpdates(new BattleModelUpdates());
                battle.getModel().setEvents(new BattleModelEvents());
                processBattleStep(battle);
                battleUpdatesProcessor.process(battle);
            }
            writeBattleToHistory(battle);
            createBattleFinishedMessages(battle);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            battle.setStageAndResetTime(BattleStage.ERROR);
            battleUpdatesProcessor.sendBattleToUpdatesQueue(battle);
        } finally {
            activeBattleStepProcessor.processError(battle);
            stopUpdatesSender(battle);
            removeBattleFromMap(battle);
            removeBattleFromRoom(battle);
            log.info("Battle finished: {}, map size {}", battle.getId(), userBattleMap.size());
        }
    }

    private void processBattleStep(Battle battle) {
        if (BattleStage.WAITING.equals(battle.getBattleStage())) {
            waitingBattleStepProcessor.processStep(battle);
        } else if (BattleStage.ACTIVE.equals(battle.getBattleStage())) {
            activeBattleStepProcessor.processStep(battle);
        }
    }

    private void removeBattleFromMap(Battle battle) {
        synchronized (userBattleMap) {
            battle.getActiveUserIds().forEach(userBattleMap::remove);
        }
    }

    private void removeBattleFromRoom(Battle battle) {
        if (battle.getRoom() != null) {
            battle.getRoom().setBattle(null);
        }
    }

    private void startUpdatesSender(Battle battle) {
        battleUpdatesSender.start(battle.getQueues().getBattleUpdatesQueue());
        //battleAggregatedSender.start(battle.getAggregated());
    }

    private void stopUpdatesSender(Battle battle) {
        battle.getQueues().getBattleUpdatesQueue().add(new BattleResponse());
        synchronized (battle.getAggregated()) {
            battle.getAggregated().setDisabled(true);
        }
    }

    private void createBattleFinishedMessages(Battle battle) {
        battle.getUserMap().values().forEach(user -> messageService.createMessage(user,
                "Battle has finished",
                new Locale().setCode(LocaleCode.BATTLE_FINISHED),
                new MessageSpecial().setUserBattleResult(createUserBattleResult(battle.getModel(), user))));
    }

    private UserBattleResult createUserBattleResult(BattleModel battleModel, User user) {
        return new ModelMapper().map(battleModel.getStatistics().get(user.getId()), UserBattleResult.class)
                .setSurvived(battleModel.getVehicles().get(user.getNickname()) != null);
    }

    private void writeBattleToHistory(Battle battle) {
        if (battle.getType().equals(BattleType.RANDOM) || battle.getType().equals(BattleType.ROOM)) {
            battleHistoryService.writeHistory(battle);
        }
    }
}
