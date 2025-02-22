package com.github.aadvorak.artilleryonline.battle.processor.drone;

import com.github.aadvorak.artilleryonline.battle.calculations.DroneCalculations;
import com.github.aadvorak.artilleryonline.battle.common.Position;
import com.github.aadvorak.artilleryonline.battle.common.Velocity;
import com.github.aadvorak.artilleryonline.battle.model.BattleModel;
import com.github.aadvorak.artilleryonline.battle.model.ShellModel;
import com.github.aadvorak.artilleryonline.battle.state.ShellState;

public class DroneGunShootProcessor {

    public static void processStep(DroneCalculations drone, BattleModel battleModel) {
        var gunState = drone.getModel().getState().getGunState();
        if (gunState.isTriggerPushed() && gunState.getLoadedShell() != null) {
            doShot(drone, battleModel);
            drone.getModel().getUpdate().setUpdated();
        }
        if (gunState.getLoadedShell() == null && gunState.getLoadingShell() == null) {
            startLoading(drone);
        } else if (gunState.getLoadingShell() != null) {
            continueLoading(drone, battleModel);
        }
    }

    private static void doShot(DroneCalculations drone, BattleModel battleModel) {
        var loadedShellSpecs = drone.getModel().getConfig().getGun().getAvailableShells()
                .get(drone.getModel().getState().getGunState().getLoadedShell());
        var shellModel = new ShellModel();
        shellModel.setId(battleModel.getIdGenerator().generate());
        if (drone.getModel().getUserId() != null) {
            shellModel.setUserId(drone.getModel().getUserId());
            battleModel.getStatistics().get(drone.getModel().getUserId()).increaseMadeShots();
        }
        shellModel.setSpecs(loadedShellSpecs);
        shellModel.setState(new ShellState()
                .setPosition(getShellInitialPosition(drone))
                .setVelocity(getShellInitialVelocity(drone, loadedShellSpecs.getVelocity()))
        );
        battleModel.getShells().put(shellModel.getId(), shellModel);
        battleModel.getUpdates().addShell(shellModel);
        drone.getModel().getState().getGunState().setLoadedShell(null);
        var gunState = drone.getModel().getState().getGunState();
        drone.getModel().getState().getAmmo().compute(gunState.getSelectedShell(), (k, ammo) -> ammo - 1);
        pushShootingDrone(drone, shellModel);
    }

    private static void startLoading(DroneCalculations drone) {
        var gunState = drone.getModel().getState().getGunState();
        var ammo = drone.getModel().getState().getAmmo().get(gunState.getSelectedShell());
        if (ammo <= 0) {
            return;
        }
        gunState.setLoadingShell(gunState.getSelectedShell());
        gunState.setLoadRemainTime(drone.getModel().getConfig().getGun().getLoadTime());
    }

    private static void continueLoading(DroneCalculations drone, BattleModel battleModel) {
        var gunState = drone.getModel().getState().getGunState();
        var loadRemainTime = gunState.getLoadRemainTime() - battleModel.getCurrentTimeStepSecs();
        if (loadRemainTime > 0) {
            gunState.setLoadRemainTime(loadRemainTime);
        } else {
            gunState.setLoadRemainTime(0.0);
            gunState.setLoadedShell(gunState.getLoadingShell());
            gunState.setLoadingShell(null);
        }
    }

    private static Position getShellInitialPosition(DroneCalculations drone) {
        var shellAngle = drone.getModel().getState().getGunAngle() + drone.getModel().getState().getPosition().getAngle();
        var gunLength = drone.getModel().getConfig().getGun().getLength();
        return drone.getModel().getState().getPosition().getCenter().shifted(gunLength, shellAngle);
    }

    private static Velocity getShellInitialVelocity(DroneCalculations drone, double shellVelocityModule) {
        var shellAngle = drone.getModel().getState().getGunAngle() + drone.getModel().getState().getPosition().getAngle();
        var gunLength = drone.getModel().getConfig().getGun().getLength();
        var pointVelocity = drone.getModel().getState().getVelocity().getPointVelocity(gunLength, shellAngle);
        var shellVelocity = new Velocity()
                .setX(shellVelocityModule * Math.cos(shellAngle))
                .setY(shellVelocityModule * Math.sin(shellAngle));
        return Velocity.sumOf(shellVelocity, pointVelocity);
    }

    private static void pushShootingDrone(DroneCalculations drone, ShellModel shellModel) {
        var droneVelocity = drone.getModel().getState().getVelocity();
        var droneMass = drone.getModel().getSpecs().getMass();
        var shellMass = shellModel.getSpecs().getMass();
        var pushCoefficient = shellMass / droneMass;
        var shellVelocity = shellModel.getState().getVelocity();
        droneVelocity
                .setX(droneVelocity.getX() - pushCoefficient * shellVelocity.getX())
                .setY(droneVelocity.getY() - pushCoefficient * shellVelocity.getY());
    }
}
