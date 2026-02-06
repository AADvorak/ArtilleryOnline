package com.github.aadvorak.artilleryonline.battle.processor.vehicle;

import com.github.aadvorak.artilleryonline.battle.calculations.BattleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.VehicleCalculations;
import com.github.aadvorak.artilleryonline.battle.common.Position;
import com.github.aadvorak.artilleryonline.battle.common.Velocity;
import com.github.aadvorak.artilleryonline.battle.model.BattleModel;
import com.github.aadvorak.artilleryonline.battle.model.ShellModel;
import com.github.aadvorak.artilleryonline.battle.model.VehicleModel;
import com.github.aadvorak.artilleryonline.battle.preset.ShellSpecsPreset;
import com.github.aadvorak.artilleryonline.battle.processor.BeforeStep2Processor;
import com.github.aadvorak.artilleryonline.battle.state.ShellState;
import org.springframework.stereotype.Component;

@Component
public class VehicleGunShootProcessor extends VehicleProcessor implements BeforeStep2Processor {

    @Override
    protected void processVehicle(VehicleCalculations vehicle, BattleCalculations battle) {
        var vehicleModel = vehicle.getModel();
        var battleModel = battle.getModel();
        var gunState = vehicleModel.getState().getGunState();
        if (gunState.isTriggerPushed() && gunState.getLoadedShell() != null) {
            doShot(vehicleModel, battleModel);
            switchFromSignalShell(vehicleModel);
            vehicleModel.getUpdate().setUpdated();
        }
        if (gunState.getLoadedShell() == null && gunState.getLoadingShell() == null) {
            if (startLoading(vehicleModel)) {
                vehicleModel.getUpdate().setUpdated();
            }
        } else if (gunState.getLoadingShell() != null) {
            continueLoading(vehicleModel, battleModel);
        }
    }

    private void doShot(VehicleModel vehicleModel, BattleModel battleModel) {
        var loadedShellSpecs = vehicleModel.getConfig().getGun().getAvailableShells()
                .get(vehicleModel.getState().getGunState().getLoadedShell());
        var shellModel = new ShellModel();
        shellModel.setId(battleModel.getIdGenerator().generate());
        shellModel.setVehicleId(vehicleModel.getId());
        if (vehicleModel.getNickname() != null) {
            shellModel.setNickname(vehicleModel.getNickname());
            battleModel.getStatistics().get(vehicleModel.getNickname()).increaseMadeShots();
        }
        shellModel.setSpecs(loadedShellSpecs);
        shellModel.setState(new ShellState()
                .setPosition(getShellInitialPosition(vehicleModel))
                .setVelocity(getShellInitialVelocity(vehicleModel, loadedShellSpecs.getVelocity()))
        );
        battleModel.getShells().put(shellModel.getId(), shellModel);
        battleModel.getUpdates().addShell(shellModel);
        vehicleModel.getState().getGunState().setLoadedShell(null);
        var gunState = vehicleModel.getState().getGunState();
        vehicleModel.getState().getAmmo().compute(gunState.getSelectedShell(), (k, ammo) -> ammo - 1);
        pushShootingVehicle(vehicleModel, shellModel);
    }

    private boolean startLoading(VehicleModel vehicleModel) {
        var gunState = vehicleModel.getState().getGunState();
        var ammo = vehicleModel.getState().getAmmo().get(gunState.getSelectedShell());
        if (ammo <= 0) {
            var anotherShell = vehicleModel.getState().getExistingAmmoShellName();
            if (anotherShell == null) {
                return false;
            }
            gunState.setSelectedShell(anotherShell);
        }
        gunState.setLoadingShell(gunState.getSelectedShell());
        gunState.setLoadRemainTime(vehicleModel.getConfig().getGun().getLoadTime());
        return true;
    }

    private void continueLoading(VehicleModel vehicleModel, BattleModel battleModel) {
        var gunState = vehicleModel.getState().getGunState();
        var loadRemainTime = gunState.getLoadRemainTime() - battleModel.getCurrentTimeStepSecs();
        if (loadRemainTime > 0) {
            gunState.setLoadRemainTime(loadRemainTime);
        } else {
            gunState.setLoadRemainTime(0.0);
            gunState.setLoadedShell(gunState.getLoadingShell());
            gunState.setLoadingShell(null);
            vehicleModel.getUpdate().setUpdated();
        }
    }

    private Position getShellInitialPosition(VehicleModel vehicleModel) {
        var shellAngle = vehicleModel.getState().getGunState().getAngle()
                + vehicleModel.getState().getPosition().getAngle();
        var gunLength = vehicleModel.getConfig().getGun().getLength();
        return vehicleModel.getState().getPosition().getCenter().shifted(gunLength, shellAngle);
    }

    private Velocity getShellInitialVelocity(VehicleModel vehicleModel, double shellVelocityModule) {
        var shellAngle = vehicleModel.getState().getGunState().getAngle()
                + vehicleModel.getState().getPosition().getAngle();
        var gunLength = vehicleModel.getConfig().getGun().getLength();
        var pointVelocity = vehicleModel.getState().getVelocity().getPointVelocity(gunLength, shellAngle);
        var shellVelocity = new Velocity()
                .setX(shellVelocityModule * Math.cos(shellAngle))
                .setY(shellVelocityModule * Math.sin(shellAngle));
        return Velocity.sumOf(shellVelocity, pointVelocity);
    }

    private void pushShootingVehicle(VehicleModel vehicleModel, ShellModel shellModel) {
        var vehicleVelocity = vehicleModel.getState().getVelocity();
        var vehicleMass = vehicleModel.getPreCalc().getMass();
        var shellMass = shellModel.getSpecs().getMass();
        var pushCoefficient = shellMass / vehicleMass;
        var shellVelocity = shellModel.getState().getVelocity();
        vehicleVelocity
                .setX(vehicleVelocity.getX() - pushCoefficient * shellVelocity.getX())
                .setY(vehicleVelocity.getY() - pushCoefficient * shellVelocity.getY());
    }

    private void switchFromSignalShell(VehicleModel vehicleModel) {
        var gunState = vehicleModel.getState().getGunState();
        if (ShellSpecsPreset.LIGHT_SGN.getName().equals(gunState.getSelectedShell())) {
            var ammo =  vehicleModel.getState().getAmmo();
            ammo.keySet().stream().sorted().findFirst().ifPresent(value -> {
                var amount = ammo.get(value);
                if (amount > 0) {
                    gunState.setSelectedShell(value);
                }
            });
        }
    }
}
