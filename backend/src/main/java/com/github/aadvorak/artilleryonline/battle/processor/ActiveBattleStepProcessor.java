package com.github.aadvorak.artilleryonline.battle.processor;

import com.github.aadvorak.artilleryonline.battle.Battle;
import com.github.aadvorak.artilleryonline.battle.BattleStage;
import com.github.aadvorak.artilleryonline.battle.calculations.BattleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.VehicleCalculations;
import com.github.aadvorak.artilleryonline.battle.common.CollisionMode;
import com.github.aadvorak.artilleryonline.battle.processor.command.CommandProcessor;
import com.github.aadvorak.artilleryonline.battle.processor.explosion.ExplosionProcessor;
import com.github.aadvorak.artilleryonline.battle.processor.shell.ShellFlyProcessor;
import com.github.aadvorak.artilleryonline.battle.processor.vehicle.*;
import com.github.aadvorak.artilleryonline.properties.ApplicationSettings;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ActiveBattleStepProcessor extends BattleStepProcessorBase implements BattleStepProcessor {

    private final ApplicationSettings applicationSettings;

    @Override
    protected void doStepLogic(Battle battle) {
        var battleModel = battle.getModel();

        readCommandsFromQueue(battle);

        var battleCalculations = new BattleCalculations()
                .setModel(battleModel)
                .setVehicles(battleModel.getVehicles().values().stream()
                        .map(vehicleModel -> new VehicleCalculations().setModel(vehicleModel))
                        .collect(Collectors.toSet()));

        battleCalculations.getVehicles().forEach(vehicleCalculations -> {
            var vehicleModel = vehicleCalculations.getModel();
            VehicleGunShootProcessor.processStep(vehicleModel, battleModel);
            VehicleGunRotateProcessor.processStep(vehicleModel, battleModel);
            VehicleTrackProcessor.processStep(vehicleModel, battleModel);
            VehicleJetProcessor.processStep(vehicleModel, battleModel);
            VehicleMoveProcessor.processStep1(vehicleCalculations, battleCalculations,
                    applicationSettings.getCollisionMode());
        });

        if (CollisionMode.getCollisionMode(applicationSettings.getCollisionMode()).equals(CollisionMode.IMPACT)) {
            CollisionsProcessor.process(battleCalculations);
        }

        battleCalculations.getVehicles().forEach(vehicleCalculations ->
                VehicleMoveProcessor.processStep2(vehicleCalculations, applicationSettings.getCollisionMode()));

        battleModel.getExplosions().values().forEach(explosionModel ->
                ExplosionProcessor.processStep(explosionModel, battleModel));

        battleModel.getShells().values().forEach(shellModel ->
                ShellFlyProcessor.processStep(shellModel, battleModel));

        if (battleModel.getUpdates().getRemoved() != null) {
            var removedExplosionIds = battleModel.getUpdates().getRemoved().getExplosionIds();
            if (removedExplosionIds != null) {
                removedExplosionIds.forEach(battleModel::removeExplosionById);
            }
            var removedShellIds = battleModel.getUpdates().getRemoved().getShellIds();
            if (removedShellIds != null) {
                removedShellIds.forEach(id -> {
                    ExplosionProcessor.initExplosion(battleModel.getShells().get(id), battleModel);
                    battleModel.removeShellById(id);
                });
            }
            var removedVehicleKeys = battleModel.getUpdates().getRemoved().getVehicleKeys();
            if (removedVehicleKeys != null) {
                removedVehicleKeys.forEach(battleModel::removeVehicleByKey);
            }
        }
    }

    @Override
    protected boolean changeStageIfNeeded(Battle battle) {
        if (super.changeStageIfNeeded(battle)) {
            return true;
        }
        if (battle.getModel().getVehicles().size() <= 1) {
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
