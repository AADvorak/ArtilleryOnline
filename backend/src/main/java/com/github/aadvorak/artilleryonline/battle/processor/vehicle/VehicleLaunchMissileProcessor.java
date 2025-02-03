package com.github.aadvorak.artilleryonline.battle.processor.vehicle;

import com.github.aadvorak.artilleryonline.battle.common.BodyPosition;
import com.github.aadvorak.artilleryonline.battle.common.BodyVelocity;
import com.github.aadvorak.artilleryonline.battle.model.BattleModel;
import com.github.aadvorak.artilleryonline.battle.model.MissileModel;
import com.github.aadvorak.artilleryonline.battle.model.VehicleModel;
import com.github.aadvorak.artilleryonline.battle.preset.MissileSpecsPreset;
import com.github.aadvorak.artilleryonline.battle.state.MissileState;

public class VehicleLaunchMissileProcessor {

    public static void launch(VehicleModel vehicleModel, BattleModel battleModel) {
        var missiles = vehicleModel.getState().getMissiles();
        if (missiles.isEmpty()) {
            return;
        }
        var missilesKey = missiles.keySet().iterator().next();
        var missilesNumber = missiles.get(missilesKey);
        if (missilesNumber < 1) {
            return;
        }
        missiles.put(missilesKey, missilesNumber - 1);
        var vehiclePosition = vehicleModel.getState().getPosition();
        var angle = vehicleModel.getState().getAngle() + Math.PI / 2;
        var specs = MissileSpecsPreset.DEFAULT.getSpecs();
        var state = new MissileState()
                .setPosition(BodyPosition.of(vehiclePosition.shifted(specs.getLength() / 2,
                        angle), angle))
                .setVelocity(BodyVelocity.of(vehicleModel.getState().getVelocity()));
        var id = battleModel.getIdGenerator().generate();
        var missileModel = new MissileModel();
        missileModel.setId(id);
        missileModel.setVehicleId(vehicleModel.getId());
        missileModel.setUserId(vehicleModel.getUserId());
        missileModel.setState(state);
        missileModel.setSpecs(specs);
        battleModel.getMissiles().put(id, missileModel);
        battleModel.getUpdates().addMissile(missileModel);
        vehicleModel.setUpdated(true);
    }
}
