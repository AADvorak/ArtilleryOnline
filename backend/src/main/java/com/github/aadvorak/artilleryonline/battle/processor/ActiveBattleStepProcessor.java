package com.github.aadvorak.artilleryonline.battle.processor;

import com.github.aadvorak.artilleryonline.battle.Battle;
import com.github.aadvorak.artilleryonline.battle.BattleStage;
import com.github.aadvorak.artilleryonline.battle.calculations.BattleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.VehicleCalculations;
import com.github.aadvorak.artilleryonline.battle.processor.command.CommandProcessor;
import com.github.aadvorak.artilleryonline.battle.processor.explosion.ExplosionProcessor;
import com.github.aadvorak.artilleryonline.battle.processor.shell.ShellFlyProcessor;
import com.github.aadvorak.artilleryonline.battle.processor.vehicle.*;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class ActiveBattleStepProcessor extends BattleStepProcessorBase implements BattleStepProcessor {

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
            VehicleMoveProcessor.processStep1(vehicleCalculations, battleCalculations);
        });

        battleCalculations.getVehicles().forEach(vehicleCalculations ->
                VehicleMoveProcessor.processStep2(vehicleCalculations, battleCalculations));

        var explosionIdsToRemove = new ArrayList<Integer>();
        battleModel.getExplosions().values().forEach(explosionModel ->
                ExplosionProcessor.processStep(explosionModel, battleModel, explosionIdsToRemove));
        if (!explosionIdsToRemove.isEmpty()) {
            battleModel.setUpdated(true);
        }
        explosionIdsToRemove.forEach(battleModel::removeExplosionById);

        var shellIdsToRemove = new ArrayList<Integer>();
        battleModel.getShells().values().forEach(shellModel ->
                ShellFlyProcessor.processStep(shellModel, battleModel, shellIdsToRemove));
        if (!shellIdsToRemove.isEmpty()) {
            battleModel.setUpdated(true);
        }
        shellIdsToRemove.forEach(id -> {
            ExplosionProcessor.initExplosion(battleModel.getShells().get(id), battleModel);
            battleModel.removeShellById(id);
        });
    }

    @Override
    protected synchronized boolean changeStageIfNeeded(Battle battle) {
        if (super.changeStageIfNeeded(battle)) {
            return true;
        }
        if (battle.getModel().getVehicles().keySet().size() <= 1) {
            battle.setStageAndResetTime(BattleStage.FINISHED);
            return true;
        }
        return false;
    }

    private void readCommandsFromQueue(Battle battle) {
        battle.getUserCommandQueues().forEach((userKey, commandQueue) -> {
            var commandsNumber = 10;
            while (commandsNumber > 0) {
                var userCommand = commandQueue.poll();
                if (userCommand == null) {
                    return;
                }
                CommandProcessor.process(userKey, userCommand, battle.getModel());
                commandsNumber--;
            }
        });
    }
}
