package com.github.aadvorak.artilleryonline.battle.processor;

import com.github.aadvorak.artilleryonline.battle.Battle;
import com.github.aadvorak.artilleryonline.battle.BattleStage;
import com.github.aadvorak.artilleryonline.battle.BattleType;
import com.github.aadvorak.artilleryonline.battle.processor.command.CommandProcessor;
import com.github.aadvorak.artilleryonline.collection.BattleTrackingMap;
import org.springframework.stereotype.Component;

@Component
public class ActiveBattleStepProcessor extends BattleStepProcessorBase implements BattleStepProcessor {

    private final AllBattleObjectsProcessor allBattleObjectsProcessor;

    public ActiveBattleStepProcessor(
            BattleTrackingMap battleTrackingMap,
            AllBattleObjectsProcessor allBattleObjectsProcessor
    ) {
        super(battleTrackingMap);
        this.allBattleObjectsProcessor = allBattleObjectsProcessor;
    }

    @Override
    protected void doStepLogic(Battle battle) {
        readCommandsFromQueue(battle);
        allBattleObjectsProcessor.process(battle);
    }

    @Override
    protected boolean changeStageIfNeeded(Battle battle) {
        if (super.changeStageIfNeeded(battle)) {
            return true;
        }
        if (battle.getType().isTeam()) {
            if (battle.getWinnerTeamId() == null) {
                var team0VehiclesCount = battle.getTeamNicknames(0).stream()
                        .filter(nickname -> battle.getModel().getVehicles().get(nickname) != null)
                        .count();
                var team1VehiclesCount = battle.getTeamNicknames(1).stream()
                        .filter(nickname -> battle.getModel().getVehicles().get(nickname) != null)
                        .count();
                if (team0VehiclesCount == 0) {
                    battle.setWinnerTeamId(1);
                } else if (team1VehiclesCount == 0) {
                    battle.setWinnerTeamId(0);
                }
            }
            if (battle.getWinnerTeamId() != null && battle.getModel().getExplosions().isEmpty()) {
                battle.setStageAndResetTime(BattleStage.FINISHED);
                return true;
            }
        }
        if (battle.getModel().getVehicles().isEmpty()
                && battle.getModel().getShells().isEmpty()
                && battle.getModel().getExplosions().isEmpty()
                && battle.getModel().getMissiles().isEmpty()) {
            battle.setStageAndResetTime(BattleStage.FINISHED);
            return true;
        }
        if (!BattleType.DRONE_HUNT.equals(battle.getType())
                && battle.getModel().getVehicles().size() == 1
                && battle.getModel().getShells().isEmpty()
                && battle.getModel().getExplosions().isEmpty()
                && battle.getModel().getMissiles().isEmpty()
                && battle.getModel().getDrones().isEmpty()) {
            battle.setStageAndResetTime(BattleStage.FINISHED);
            return true;
        }
        return false;
    }

    private void readCommandsFromQueue(Battle battle) {
        battle.getQueues().getUserCommandQueues().forEach((userId, commandQueue) -> {
            var commandsNumber = 10;
            while (commandsNumber > 0) {
                var userCommand = commandQueue.poll();
                if (userCommand == null) {
                    return;
                }
                CommandProcessor.process(battle.getUserMap().get(userId).getNickname(),
                        userCommand, battle.getModel());
                commandsNumber--;
            }
        });
    }
}
