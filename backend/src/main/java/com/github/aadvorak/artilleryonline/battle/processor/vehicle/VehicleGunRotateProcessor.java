package com.github.aadvorak.artilleryonline.battle.processor.vehicle;

import com.github.aadvorak.artilleryonline.battle.Battle;
import com.github.aadvorak.artilleryonline.battle.common.MovingDirection;
import com.github.aadvorak.artilleryonline.battle.model.VehicleModel;

public class VehicleGunRotateProcessor {

    public static void processStep(VehicleModel vehicleModel) {
        var rotatingDirection = vehicleModel.getState().getGunRotatingDirection();
        if (rotatingDirection == null) {
            return;
        }
        var gunAngle = vehicleModel.getState().getGunAngle();
        gunAngle += (MovingDirection.RIGHT.equals(rotatingDirection) ? -1 : 1)
                * Battle.getTimeStepSecs()
                * vehicleModel.getConfig().getGun().getRotationVelocity();
        var maxAngle = vehicleModel.getSpecs().getMaxAngle();
        if (gunAngle > maxAngle) {
            gunAngle = maxAngle;
        }
        var minAngle = vehicleModel.getSpecs().getMinAngle();
        if (gunAngle < minAngle) {
            gunAngle = minAngle;
        }
        vehicleModel.getState().setGunAngle(gunAngle);
    }
}
