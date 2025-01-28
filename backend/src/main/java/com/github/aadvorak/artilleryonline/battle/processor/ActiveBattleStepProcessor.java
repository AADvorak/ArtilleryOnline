package com.github.aadvorak.artilleryonline.battle.processor;

import com.github.aadvorak.artilleryonline.battle.Battle;
import com.github.aadvorak.artilleryonline.battle.BattleStage;
import com.github.aadvorak.artilleryonline.battle.calculations.BattleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.MissileCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.ShellCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.VehicleCalculations;
import com.github.aadvorak.artilleryonline.battle.processor.command.CommandProcessor;
import com.github.aadvorak.artilleryonline.battle.processor.explosion.ExplosionProcessor;
import com.github.aadvorak.artilleryonline.battle.processor.missile.MissileFlyProcessor;
import com.github.aadvorak.artilleryonline.battle.processor.shell.ShellFlyProcessor;
import com.github.aadvorak.artilleryonline.battle.processor.shell.collisions.ShellsCollisionsProcessor;
import com.github.aadvorak.artilleryonline.battle.processor.vehicle.*;
import com.github.aadvorak.artilleryonline.battle.processor.vehicle.collisions.VehiclesCollisionsProcessor;
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
                        .map(VehicleCalculations::new)
                        .collect(Collectors.toSet()))
                .setShells(battleModel.getShells().values().stream()
                        .map(ShellCalculations::new)
                        .collect(Collectors.toSet()))
                .setMissiles(battleModel.getMissiles().values().stream()
                        .map(MissileCalculations::new)
                        .collect(Collectors.toSet()));

        battleModel.getExplosions().values().forEach(explosionModel ->
                ExplosionProcessor.processStep(explosionModel, battleModel));

        battleCalculations.getShells().forEach(shellCalculations ->
                ShellFlyProcessor.processStep1(shellCalculations, battleModel));

        battleCalculations.getMissiles().forEach(missileCalculations ->
                MissileFlyProcessor.processStep1(missileCalculations, battleModel));

        battleCalculations.getVehicles().forEach(vehicleCalculations -> {
            var vehicleModel = vehicleCalculations.getModel();
            VehicleGunRotateProcessor.processStep(vehicleModel, battleModel);
            VehicleTrackProcessor.processStep(vehicleModel, battleModel);
            VehicleJetProcessor.processStep(vehicleModel, battleModel);
            VehicleMoveProcessor.processStep1(vehicleCalculations, battleCalculations);
        });

        ShellsCollisionsProcessor.process(battleCalculations);
        VehiclesCollisionsProcessor.process(battleCalculations,
                applicationSettings.getAdditionalResolveCollisionsIterationsNumber());

        battleCalculations.getVehicles().forEach(vehicleCalculations ->
                VehicleGunShootProcessor.processStep(vehicleCalculations.getModel(), battleModel));

        battleCalculations.getVehicles().forEach(VehicleMoveProcessor::processStep2);
        battleCalculations.getShells().forEach(shellCalculations ->
                ShellFlyProcessor.processStep2(shellCalculations, battleModel));
        battleCalculations.getMissiles().forEach(missileCalculations ->
                MissileFlyProcessor.processStep2(missileCalculations, battleModel));

        if (battleModel.getUpdates().getRemoved() != null) {
            var removedExplosionIds = battleModel.getUpdates().getRemoved().getExplosionIds();
            if (removedExplosionIds != null) {
                removedExplosionIds.forEach(battleModel::removeExplosionById);
            }
            var removedShellIds = battleModel.getUpdates().getRemoved().getShellIds();
            if (removedShellIds != null) {
                removedShellIds.forEach(id -> {
                    var shellModel = battleModel.getShells().get(id);
                    ExplosionProcessor.initExplosion(shellModel.getState().getPosition(),
                            shellModel.getSpecs().getRadius(), battleModel);
                    battleModel.removeShellById(id);
                });
            }
            var removedMissileIds = battleModel.getUpdates().getRemoved().getMissileIds();
            if (removedMissileIds != null) {
                removedMissileIds.forEach(id -> {
                    var missileModel = battleModel.getMissiles().get(id);
                    ExplosionProcessor.initExplosion(missileModel.getState().getPosition().getCenter(),
                            missileModel.getSpecs().getRadius(), battleModel);
                    battleModel.removeMissileById(id);
                });
            }
            var removedVehicleKeys = battleModel.getUpdates().getRemoved().getVehicleKeys();
            if (removedVehicleKeys != null) {
                removedVehicleKeys.forEach(key -> {
                    var vehicleModel = battleModel.getVehicles().get(key);
                    ExplosionProcessor.initExplosion(vehicleModel.getState().getPosition(),
                            vehicleModel.getSpecs().getRadius(), battleModel);
                    battleModel.removeVehicleByKey(key);
                });
            }
        }
    }

    @Override
    protected boolean changeStageIfNeeded(Battle battle) {
        if (super.changeStageIfNeeded(battle)) {
            return true;
        }
        if (battle.getModel().getVehicles().size() <= 1 && battle.getModel().getShells().isEmpty()
                && battle.getModel().getExplosions().isEmpty()) {
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
