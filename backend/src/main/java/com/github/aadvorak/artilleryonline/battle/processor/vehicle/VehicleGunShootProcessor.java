package com.github.aadvorak.artilleryonline.battle.processor.vehicle;

import com.github.aadvorak.artilleryonline.battle.common.Position;
import com.github.aadvorak.artilleryonline.battle.model.BattleModel;
import com.github.aadvorak.artilleryonline.battle.model.ShellModel;
import com.github.aadvorak.artilleryonline.battle.model.VehicleModel;
import com.github.aadvorak.artilleryonline.battle.state.ShellState;

public class VehicleGunShootProcessor {

    public static void processStep(VehicleModel vehicleModel, BattleModel battleModel) {
        var gunState = vehicleModel.getState().getGunState();
        if (gunState.isTriggerPushed() && gunState.getLoadedShell() != null) {
            doShot(vehicleModel, battleModel);
            battleModel.setUpdated(true);
        }
        if (gunState.getLoadedShell() == null && gunState.getLoadingShell() == null) {
            startLoading(vehicleModel);
            vehicleModel.setUpdated(true);
        } else if (gunState.getLoadingShell() != null) {
            continueLoading(vehicleModel, battleModel);
        }
    }

    private static void doShot(VehicleModel vehicleModel, BattleModel battleModel) {
        var loadedShellSpecs = vehicleModel.getConfig().getGun().getAvailableShells()
                .get(vehicleModel.getState().getGunState().getLoadedShell());
        var shellModel = new ShellModel();
        shellModel.setId(battleModel.getIdGenerator().generate());
        shellModel.setSpecs(loadedShellSpecs);
        shellModel.setState(new ShellState()
                .setAngle(vehicleModel.getState().getGunAngle() + vehicleModel.getState().getAngle())
                .setPosition(getShellInitialPosition(vehicleModel))
                .setVelocity(loadedShellSpecs.getVelocity()));
        battleModel.getShells().put(shellModel.getId(), shellModel);
        vehicleModel.getState().getGunState().setLoadedShell(null);
        var gunState = vehicleModel.getState().getGunState();
        vehicleModel.getState().getAmmo().compute(gunState.getSelectedShell(), (k, ammo) -> ammo - 1);
        pushShootingVehicle(vehicleModel, shellModel);
    }

    private static void startLoading(VehicleModel vehicleModel) {
        var gunState = vehicleModel.getState().getGunState();
        var ammo = vehicleModel.getState().getAmmo().get(gunState.getSelectedShell());
        if (ammo <= 0) {
            return;
        }
        gunState.setLoadingShell(gunState.getSelectedShell());
        gunState.setLoadRemainTime(vehicleModel.getConfig().getGun().getLoadTime());
    }

    private static void continueLoading(VehicleModel vehicleModel, BattleModel battleModel) {
        var gunState = vehicleModel.getState().getGunState();
        var loadRemainTime = gunState.getLoadRemainTime() - battleModel.getCurrentTimeStepSecs();
        if (loadRemainTime > 0) {
            gunState.setLoadRemainTime(loadRemainTime);
        } else {
            gunState.setLoadRemainTime(0.0);
            gunState.setLoadedShell(gunState.getLoadingShell());
            gunState.setLoadingShell(null);
            vehicleModel.setUpdated(true);
        }
    }

    private static Position getShellInitialPosition(VehicleModel vehicleModel) {
        var vehiclePosition = vehicleModel.getState().getPosition();
        var gunAngle = vehicleModel.getState().getGunAngle();
        var angle = vehicleModel.getState().getAngle();
        var gunLength = vehicleModel.getConfig().getGun().getLength();
        return new Position()
                .setX(vehiclePosition.getX() + gunLength * Math.cos(gunAngle + angle))
                .setY(vehiclePosition.getY() + gunLength * Math.sin(gunAngle + angle));
    }

    private static void pushShootingVehicle(VehicleModel vehicleModel, ShellModel shellModel) {
        var vehicleVelocity = vehicleModel.getState().getVelocity();
        var pushCoefficient = shellModel.getSpecs().getPushCoefficient();
        var shellVelocity = shellModel.getState().getVelocity();
        var shellAngle = shellModel.getState().getAngle();
        vehicleVelocity
                .setX(vehicleVelocity.getX() - pushCoefficient * shellVelocity * Math.cos(shellAngle))
                .setY(vehicleVelocity.getY() - pushCoefficient * shellVelocity * Math.sin(shellAngle));
    }
}
