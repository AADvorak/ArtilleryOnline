package com.github.aadvorak.artilleryonline.battle.processor.vehicle;

import com.github.aadvorak.artilleryonline.battle.Battle;
import com.github.aadvorak.artilleryonline.battle.calculations.BattleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.VehicleCalculations;
import com.github.aadvorak.artilleryonline.battle.common.MovingDirection;
import com.github.aadvorak.artilleryonline.battle.processor.AfterStep1Processor;
import org.springframework.stereotype.Component;

@Component
public class VehicleGunRotateProcessor extends VehicleProcessor implements AfterStep1Processor {

    @Override
    protected void processVehicle(VehicleCalculations vehicle, BattleCalculations battle) {
        var gunState = vehicle.getModel().getState().getGunState();
        if (vehicle.getNextPosition().isAngleNormalized()) {
            var angleDiff = vehicle.getNextPosition().getAngle() - vehicle.getModel().getState().getPosition().getAngle();
            var targetAngle = gunState.getTargetAngle();
            if (angleDiff > 0) {
                gunState.setTargetAngle(targetAngle + Math.PI * 2);
            }
            if (angleDiff < 0) {
                gunState.setTargetAngle(targetAngle - Math.PI * 2);
            }
        }
        var rotatingDirection = gunState.getRotatingDirection();
        var rotatingVelocity = vehicle.getModel().getConfig().getGun().getRotationVelocity();
        var maxGunAngle = vehicle.getModel().getSpecs().getMaxAngle();
        var minGunAngle = vehicle.getModel().getSpecs().getMinAngle();
        var vehicleAngle = vehicle.getNextPosition().getAngle();
        var targetAngle = gunState.isFixed()
                ? vehicleAngle + gunState.getAngle()
                : gunState.getTargetAngle();
        if (rotatingDirection != null) {
            targetAngle += (MovingDirection.RIGHT.equals(rotatingDirection) ? -1 : 1)
                    * battle.getModel().getCurrentTimeStepSecs() * rotatingVelocity;
        }
        targetAngle = restrictValue(targetAngle, minGunAngle + vehicleAngle, maxGunAngle + vehicleAngle);
        gunState.setTargetAngle(targetAngle);
        if (gunState.isFixed()) {
            gunState.setAngle(targetAngle - vehicleAngle);
        } else {
            var gunAngle = gunState.getAngle();
            var angleDiff = targetAngle - vehicleAngle - gunAngle;
            if (Math.abs(angleDiff) > rotatingVelocity * Battle.TIME_STEP_MS / 1000) {
                var angleStep = Math.signum(angleDiff) * rotatingVelocity * battle.getModel().getCurrentTimeStepSecs();
                gunAngle += Math.min(angleStep, angleDiff);
                gunAngle = restrictValue(gunAngle, minGunAngle, maxGunAngle);
                gunState.setAngle(gunAngle);
            }
        }
    }

    private double restrictValue(double value, double min, double max) {
        if (value < min) {
            return min;
        }
        return Math.min(value, max);
    }
}
