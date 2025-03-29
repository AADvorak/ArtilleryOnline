package com.github.aadvorak.artilleryonline.battle.processor.vehicle;

import com.github.aadvorak.artilleryonline.battle.common.MovingDirection;
import com.github.aadvorak.artilleryonline.battle.model.BattleModel;
import com.github.aadvorak.artilleryonline.battle.model.VehicleModel;

public class VehicleGunRotateProcessor {

    public static void processStep(VehicleModel vehicleModel, BattleModel battleModel) {
        var rotatingDirection = vehicleModel.getState().getGunRotatingDirection();
        if (rotatingDirection == null) {
            return;
        }
        var gunAngle = vehicleModel.getState().getGunState().getAngle();
        gunAngle += (MovingDirection.RIGHT.equals(rotatingDirection) ? -1 : 1)
                * battleModel.getCurrentTimeStepSecs()
                * vehicleModel.getConfig().getGun().getRotationVelocity();
        var maxAngle = vehicleModel.getSpecs().getMaxAngle();
        if (gunAngle > maxAngle) {
            gunAngle = maxAngle;
        }
        var minAngle = vehicleModel.getSpecs().getMinAngle();
        if (gunAngle < minAngle) {
            gunAngle = minAngle;
        }
        vehicleModel.getState().getGunState().setAngle(gunAngle);
    }
}
