package com.github.aadvorak.artilleryonline.battle.processor;

import com.github.aadvorak.artilleryonline.battle.Battle;
import com.github.aadvorak.artilleryonline.battle.BattleStage;
import com.github.aadvorak.artilleryonline.battle.processor.command.CommandProcessor;
import com.github.aadvorak.artilleryonline.battle.processor.explosion.ExplosionProcessor;
import com.github.aadvorak.artilleryonline.battle.processor.shell.ShellFlyProcessor;
import com.github.aadvorak.artilleryonline.battle.processor.vehicle.VehicleGunRotateProcessor;
import com.github.aadvorak.artilleryonline.battle.processor.vehicle.VehicleGunShootProcessor;
import com.github.aadvorak.artilleryonline.battle.processor.vehicle.VehicleMoveProcessor;
import com.github.aadvorak.artilleryonline.battle.processor.vehicle.VehicleTrackProcessor;

import java.util.ArrayList;

public class ActiveBattleStepProcessor extends BattleStepProcessorBase implements BattleStepProcessor {

    @Override
    protected void doStepLogic(Battle battle) {
        var battleModel = battle.getModel();

        readCommandsFromQueue(battle);

        battleModel.getVehicles().values().forEach(vehicleModel -> {
            VehicleGunShootProcessor.processStep(vehicleModel, battleModel);
            VehicleMoveProcessor.processStep(vehicleModel, battleModel);
            VehicleGunRotateProcessor.processStep(vehicleModel, battleModel);
            VehicleTrackProcessor.processStep(vehicleModel, battleModel);
        });

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
