package com.github.aadvorak.artilleryonline.battle.calculator;

import com.github.aadvorak.artilleryonline.battle.calculations.DroneCalculations;
import com.github.aadvorak.artilleryonline.battle.common.Position;
import com.github.aadvorak.artilleryonline.battle.model.BattleModel;
import com.github.aadvorak.artilleryonline.battle.utils.GeometryUtils;

import java.util.Set;
import java.util.stream.Collectors;

public class DroneTargetCalculator {

    public static void calculate(DroneCalculations drone, BattleModel battleModel) {
        if (drone.getModel().isDestroyed()) {
            return;
        }
        var ammo = drone.getModel().getState().getAmmo().values().iterator().next();
        var targets = getTargets(drone, battleModel, ammo);
        if (targets.isEmpty()) {
            return;
        }
        var dronePosition = drone.getModel().getState().getPosition().getCenter();
        var xDiffMap = targets.stream()
                .collect(Collectors.toMap(
                        position -> position.getX() - dronePosition.getX(),
                        position -> position));
        var iterator = xDiffMap.keySet().iterator();
        var minXDiff = iterator.next();
        while (iterator.hasNext()) {
            var xDiff = iterator.next();
            if (Math.abs(xDiff) < Math.abs(minXDiff)) {
                minXDiff = xDiff;
            }
        }
        var targetPosition = xDiffMap.get(minXDiff);
        var gunAngle = drone.getModel().getState().getGunAngle() + drone.getModel().getState().getPosition().getAngle();
        var angleDiff = GeometryUtils.calculateAngleDiff(gunAngle, dronePosition.angleTo(targetPosition));
        drone.getModel().getState().getGunState().setTriggerPushed(!drone.getModel().isDestroyed()
                && Math.abs(angleDiff) < Math.PI / 32
                && dronePosition.distanceTo(targetPosition) < drone.getModel().getSpecs().getFlyHeight());
        drone.setTarget(new DroneCalculations.Target()
                .setXDiff(minXDiff)
                .setAngleDiff(angleDiff)
        );
    }

    private static Set<Position> getTargets(DroneCalculations drone, BattleModel battleModel, Integer ammo) {
        if (ammo > 0) {
            return TargetCalculator.calculatePositions(drone.getModel().getVehicleId(), battleModel);
        } else {
            return battleModel.getVehicles().values().stream()
                    .filter(vehicleModel -> drone.getModel().getVehicleId() != null
                            && vehicleModel.getId() == drone.getModel().getVehicleId())
                    .map(vehicleModel -> vehicleModel.getState().getPosition().getCenter())
                    .collect(Collectors.toSet());
        }
    }
}
