package com.github.aadvorak.artilleryonline.battle.processor.vehicle;

import com.github.aadvorak.artilleryonline.battle.Battle;
import com.github.aadvorak.artilleryonline.battle.common.MovingDirection;
import com.github.aadvorak.artilleryonline.battle.model.BattleModel;
import com.github.aadvorak.artilleryonline.battle.model.VehicleModel;

public class VehicleGunRotateProcessor {

    public static void processStep(VehicleModel vehicleModel, BattleModel battleModel) {
        var rotatingDirection = vehicleModel.getState().getGunState().getRotatingDirection();
        var rotatingVelocity = vehicleModel.getConfig().getGun().getRotationVelocity();
        var maxGunAngle = vehicleModel.getSpecs().getMaxAngle();
        var minGunAngle = vehicleModel.getSpecs().getMinAngle();
        var vehicleAngle = vehicleModel.getState().getPosition().getAngle();
        var targetAngle = vehicleModel.getState().getGunState().getTargetAngle();
        if (rotatingDirection != null) {
            targetAngle += (MovingDirection.RIGHT.equals(rotatingDirection) ? -1 : 1)
                    * battleModel.getCurrentTimeStepSecs() * rotatingVelocity;
        }
        targetAngle = restrictValue(targetAngle, minGunAngle + vehicleAngle, maxGunAngle + vehicleAngle);
        vehicleModel.getState().getGunState().setTargetAngle(targetAngle);
        var gunAngle = vehicleModel.getState().getGunState().getAngle();
        var angleDiff = targetAngle - vehicleAngle - gunAngle;
        if (Math.abs(angleDiff) > rotatingVelocity * Battle.TIME_STEP_MS / 1000) {
            var angleStep = Math.signum(angleDiff) * rotatingVelocity * battleModel.getCurrentTimeStepSecs();
            gunAngle += Math.min(angleStep, angleDiff);
            gunAngle = restrictValue(gunAngle, minGunAngle, maxGunAngle);
            vehicleModel.getState().getGunState().setAngle(gunAngle);
        }
    }

    private static double restrictValue(double value, double min, double max) {
        if (value < min) {
            return min;
        }
        return Math.min(value, max);
    }
}
