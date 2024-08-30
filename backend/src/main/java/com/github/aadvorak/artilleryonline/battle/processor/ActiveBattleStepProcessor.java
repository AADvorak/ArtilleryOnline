package com.github.aadvorak.artilleryonline.battle.processor;

import com.github.aadvorak.artilleryonline.battle.Battle;
import com.github.aadvorak.artilleryonline.battle.processor.shell.ShellFlyProcessor;
import com.github.aadvorak.artilleryonline.battle.processor.vehicle.VehicleGunProcessor;
import com.github.aadvorak.artilleryonline.battle.processor.vehicle.VehicleMoveProcessor;

public class ActiveBattleStepProcessor extends BattleStepProcessorBase implements BattleStepProcessor {

    @Override
    protected void doStepLogic(Battle battle) {
        // чтение команд из очереди
        // логика изменения ландшафта и техники от попадания снарядов
        battle.getModel().getVehicles().values().forEach(vehicleModel -> {
            VehicleMoveProcessor.processStep(vehicleModel, battle.getModel());
            VehicleGunProcessor.processStep(vehicleModel, battle.getModel());
        });
        battle.getModel().getShells().forEach(shellModel ->
                ShellFlyProcessor.processStep(shellModel, battle.getModel()));
    }
}
