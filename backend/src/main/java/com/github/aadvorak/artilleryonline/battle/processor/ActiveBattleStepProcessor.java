package com.github.aadvorak.artilleryonline.battle.processor;

import com.github.aadvorak.artilleryonline.battle.Battle;
import com.github.aadvorak.artilleryonline.battle.BattleStage;
import com.github.aadvorak.artilleryonline.battle.processor.command.CommandProcessor;
import com.github.aadvorak.artilleryonline.battle.processor.shell.ShellFlyProcessor;
import com.github.aadvorak.artilleryonline.battle.processor.vehicle.VehicleGunRotateProcessor;
import com.github.aadvorak.artilleryonline.battle.processor.vehicle.VehicleGunShootProcessor;
import com.github.aadvorak.artilleryonline.battle.processor.vehicle.VehicleMoveProcessor;

public class ActiveBattleStepProcessor extends BattleStepProcessorBase implements BattleStepProcessor {

    @Override
    protected void doStepLogic(Battle battle) {
        readCommandsFromQueue(battle);
        // логика изменения ландшафта от попадания снарядов
        battle.getModel().getVehicles().values().forEach(vehicleModel -> {
            VehicleGunShootProcessor.processStep(vehicleModel, battle.getModel());
            VehicleMoveProcessor.processStep(vehicleModel, battle.getModel());
            VehicleGunRotateProcessor.processStep(vehicleModel);
        });
        battle.getModel().getShells().forEach(shellModel ->
                ShellFlyProcessor.processStep(shellModel, battle.getModel()));
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
        battle.getUserCommands().forEach((userKey, commandQueue) -> {
            var commandsNumber = 10;
            while (commandsNumber > 0) {
                var userCommand = commandQueue.peek();
                if (userCommand == null) {
                    return;
                }
                CommandProcessor.process(userKey, userCommand, battle.getModel());
                commandsNumber--;
            }
        });
    }
}
