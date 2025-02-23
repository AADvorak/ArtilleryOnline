package com.github.aadvorak.artilleryonline.battle.calculator;

import com.github.aadvorak.artilleryonline.battle.calculations.DroneCalculations;
import com.github.aadvorak.artilleryonline.battle.model.BattleModel;
import com.github.aadvorak.artilleryonline.battle.utils.GeometryUtils;

import java.util.stream.Collectors;

public class DroneTargetCalculator {

    public static void calculate(DroneCalculations drone, BattleModel battleModel) {
        var ammo = drone.getModel().getState().getAmmo().values().iterator().next();
        var targetsStream = battleModel.getVehicles().values().stream();
        if (ammo > 0) {
            targetsStream = targetsStream.filter(vehicleModel ->
                    vehicleModel.getId() != drone.getModel().getVehicleId());
        } else {
            targetsStream = targetsStream.filter(vehicleModel ->
                    vehicleModel.getId() == drone.getModel().getVehicleId());
        }
        var targets = targetsStream.collect(Collectors.toSet());
        if (targets.isEmpty()) {
            return;
        }
        var dronePosition = drone.getModel().getState().getPosition().getCenter();
        var xDiffMap = targets.stream()
                .collect(Collectors.toMap(
                        vehicle -> vehicle.getState().getPosition().getX() - dronePosition.getX(),
                        vehicle -> vehicle.getState().getPosition().getCenter()));
        var iterator = xDiffMap.keySet().iterator();
        var minXDiff = iterator.next();
        while (iterator.hasNext()) {
            var xDiff = iterator.next();
            if (Math.abs(xDiff) < Math.abs(minXDiff)) {
                minXDiff = xDiff;
            }
        }
        var targetPosition = xDiffMap.get(minXDiff);
        var targetAngle = dronePosition.angleTo(targetPosition);
        var gunAngle = drone.getModel().getState().getGunAngle() + drone.getModel().getState().getPosition().getAngle();
        var angleDiff = GeometryUtils.calculateAngleDiff(gunAngle, targetAngle);
        drone.getModel().getState().getGunState().setTriggerPushed(Math.abs(angleDiff) < Math.PI / 32
                && dronePosition.distanceTo(targetPosition) < drone.getModel().getSpecs().getFlyHeight());
        drone.setTarget(new DroneCalculations.Target()
                .setXDiff(minXDiff)
                .setPosition(targetPosition)
                .setAngle(targetAngle)
                .setAngleDiff(angleDiff)
        );
    }
}
