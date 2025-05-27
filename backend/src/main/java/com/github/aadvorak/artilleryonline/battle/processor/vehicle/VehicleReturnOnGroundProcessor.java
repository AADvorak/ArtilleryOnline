package com.github.aadvorak.artilleryonline.battle.processor.vehicle;

import com.github.aadvorak.artilleryonline.battle.calculations.BattleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.VehicleCalculations;

public class VehicleReturnOnGroundProcessor {

    public static void process(VehicleCalculations vehicle, BattleCalculations battle, long currentTime) {
        var angle = vehicle.getModel().getState().getPosition().getAngle();
        if (angle > Math.PI / 2 || angle < -Math.PI / 2) {
            if (vehicle.getModel().getTurnedOverTime() == null) {
                vehicle.getModel().setTurnedOverTime(currentTime);
            } else if (currentTime - vehicle.getModel().getTurnedOverTime()
                    >= vehicle.getModel().getSpecs().getTrackRepairTime() * 1000 && vehicle.getCollisions().isEmpty()) {
                var position = vehicle.getModel().getState().getPosition();
                var minX = battle.getModel().getRoom().getSpecs().getLeftBottom().getX();
                var maxX = battle.getModel().getRoom().getSpecs().getRightTop().getX();
                var maxRadius = vehicle.getModel().getPreCalc().getMaxRadius();
                if (position.getX() - maxRadius < minX) {
                    position.setX(minX + maxRadius);
                }
                if (position.getX() + maxRadius > maxX) {
                    position.setX(maxX - maxRadius);
                }
                VehicleOnGroundProcessor.estimateVehicleAngleByPosition(vehicle.getModel(), battle.getModel().getRoom());
                VehicleOnGroundProcessor.correctVehiclePositionAndAngleOnGround(vehicle.getModel(), battle.getModel().getRoom());
                vehicle.getModel().getState().getGunState().setTargetAngle(
                        vehicle.getModel().getState().getPosition().getAngle()
                        + vehicle.getModel().getState().getGunState().getAngle());
                vehicle.getModel().setUpdated(true);
            }
        } else {
            vehicle.getModel().setTurnedOverTime(null);
        }
    }
}
