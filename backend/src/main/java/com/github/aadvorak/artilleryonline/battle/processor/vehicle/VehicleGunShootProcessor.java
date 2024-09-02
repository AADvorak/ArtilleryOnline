package com.github.aadvorak.artilleryonline.battle.processor.vehicle;

import com.github.aadvorak.artilleryonline.battle.Battle;
import com.github.aadvorak.artilleryonline.battle.model.BattleModel;
import com.github.aadvorak.artilleryonline.battle.model.ShellModel;
import com.github.aadvorak.artilleryonline.battle.model.VehicleModel;
import com.github.aadvorak.artilleryonline.battle.state.ShellState;

public class VehicleGunShootProcessor {

    public static void processStep(VehicleModel vehicleModel, BattleModel battleModel) {
        var gunState = vehicleModel.getState().getGunState();
        if (gunState.isTriggerPushed() && gunState.getLoadedShell() != null) {
            doShot(vehicleModel, battleModel);
        }
        if (gunState.getLoadedShell() == null && gunState.getLoadingShell() == null) {
            startLoading(vehicleModel);
        } else if (gunState.getLoadingShell() != null) {
            continueLoading(vehicleModel);
        }
    }

    private static void doShot(VehicleModel vehicleModel, BattleModel battleModel) {
        var loadedShellSpecs = vehicleModel.getConfig().getGun().getAvailableShells()
                .get(vehicleModel.getState().getGunState().getLoadedShell());
        var shellModel = new ShellModel();
        shellModel.setId(battleModel.getIdGenerator().generate());
        shellModel.setSpecs(loadedShellSpecs);
        shellModel.setState(new ShellState()
                .setAngle(vehicleModel.getState().getGunAngle())
                .setPosition(vehicleModel.getState().getPosition())
                .setVelocity(loadedShellSpecs.getVelocity()));
        var shells = battleModel.getShells();
        shells.add(shellModel);
        battleModel.setShells(shells);
        vehicleModel.getState().getGunState().setLoadedShell(null);
    }

    private static void startLoading(VehicleModel vehicleModel) {
        var gunState = vehicleModel.getState().getGunState();
        var ammo = vehicleModel.getState().getAmmo().get(gunState.getSelectedShell());
        if (ammo <= 0) {
            return;
        }
        vehicleModel.getState().getAmmo().put(gunState.getSelectedShell(), ammo - 1);
        gunState.setLoadingShell(gunState.getSelectedShell());
        gunState.setLoadRemainTime(vehicleModel.getConfig().getGun().getLoadTime());
    }

    private static void continueLoading(VehicleModel vehicleModel) {
        var gunState = vehicleModel.getState().getGunState();
        var loadRemainTime = gunState.getLoadRemainTime() - Battle.getTimeStepSecs();
        if (loadRemainTime > 0) {
            gunState.setLoadRemainTime(loadRemainTime);
        } else {
            gunState.setLoadRemainTime(0.0);
            gunState.setLoadedShell(gunState.getLoadingShell());
            gunState.setLoadingShell(null);
        }
    }
}
