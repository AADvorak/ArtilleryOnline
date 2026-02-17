package com.github.aadvorak.artilleryonline.battle.processor.vehicle;

import com.github.aadvorak.artilleryonline.battle.common.BodyPosition;
import com.github.aadvorak.artilleryonline.battle.common.BodyVelocity;
import com.github.aadvorak.artilleryonline.battle.model.BattleModel;
import com.github.aadvorak.artilleryonline.battle.model.MissileModel;
import com.github.aadvorak.artilleryonline.battle.model.VehicleModel;
import com.github.aadvorak.artilleryonline.battle.state.MissileState;

public class VehicleLaunchMissileProcessor {

    public static void launch(VehicleModel vehicleModel, BattleModel battleModel) {
        var launcherState = vehicleModel.getState().getMissileLauncherState();
        var launcherSpecs = vehicleModel.getConfig().getMissileLauncher();
        if (launcherSpecs == null
                || launcherState == null
                || launcherState.getRemainMissiles() < 1
                || launcherState.getPrepareToLaunchRemainTime() > 0) {
            return;
        }
        var vehiclePosition = vehicleModel.getState().getPosition();
        var angle = vehicleModel.getState().getLaunchAngle();
        if (angle == null) {
            return;
        }
        launcherState.setRemainMissiles(launcherState.getRemainMissiles() - 1);
        launcherState.setPrepareToLaunchRemainTime(launcherSpecs.getPrepareToLaunchTime());
        var specs = launcherSpecs.getMissiles();
        var state = new MissileState()
                .setPosition(BodyPosition.of(vehiclePosition.getCenter().shifted(specs.getLength() / 2,
                        angle), angle))
                .setVelocity(BodyVelocity.of(vehicleModel.getState().getVelocity()));
        var id = battleModel.getIdGenerator().generate();
        var missileModel = new MissileModel();
        missileModel.setId(id);
        missileModel.setVehicleId(vehicleModel.getId());
        missileModel.setNickname(vehicleModel.getNickname());
        missileModel.setState(state);
        missileModel.setSpecs(specs);
        battleModel.getMissiles().put(id, missileModel);
        battleModel.getUpdates().addMissile(missileModel);
        vehicleModel.getUpdate().setUpdated();
    }
}
