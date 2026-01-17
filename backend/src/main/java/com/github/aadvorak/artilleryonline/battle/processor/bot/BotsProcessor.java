package com.github.aadvorak.artilleryonline.battle.processor.bot;

import com.github.aadvorak.artilleryonline.battle.calculations.BattleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.VehicleCalculations;
import com.github.aadvorak.artilleryonline.battle.common.MovingDirection;
import com.github.aadvorak.artilleryonline.battle.utils.GeometryUtils;

import java.util.Set;
import java.util.stream.Collectors;

public class BotsProcessor {

    private final TargetDataCalculator targetDataCalculator =  new TargetDataCalculator();

    public void process(Set<Integer> botVehicleIds, BattleCalculations battle) {
        battle.getVehicles().stream()
                .filter(vehicle -> botVehicleIds.contains(vehicle.getId()))
                .forEach(vehicle -> processVehicle(vehicle, battle));
    }

    private void processVehicle(VehicleCalculations vehicle, BattleCalculations battle) {
        var state = vehicle.getModel().getState();
        var oldTriggerPushed = state.getGunState().isTriggerPushed();
        var oldGunRotatingDirection = state.getGunState().getRotatingDirection();
        var oldJetActive = state.getJetState().isActive();
        var oldMovingDirection = state.getMovingDirection();
        var gunAngle = state.getPosition().getAngle() + state.getGunState().getAngle();
        var targetData = targetDataCalculator.calculate(vehicle, battle);
        state.getGunState().setTriggerPushed(targetData != null && targetData.armor() != null); // todo check penetration or use HE
        state.setMovingDirection(null);
        if (gunAngle < Math.PI / 4) {
            state.getGunState().setRotatingDirection(MovingDirection.LEFT);
        } else if (gunAngle > 3 * Math.PI / 4) {
            state.getGunState().setRotatingDirection(MovingDirection.RIGHT);
        } else if (targetData == null) {
            state.getGunState().setRotatingDirection(gunAngle > Math.PI / 2 ? MovingDirection.RIGHT : MovingDirection.LEFT);
        } else if (targetData.armor() == null) {
            var otherVehiclePositions = battle.getVehicles().stream()
                    .filter(item -> !vehicle.getId().equals(item.getId()))
                    .map(VehicleCalculations::getPosition)
                    .collect(Collectors.toSet());
            var closestPosition = GeometryUtils.findClosestPosition(targetData.contact().position(), otherVehiclePositions);
            if (closestPosition != null) {
                var vehicleX = vehicle.getPosition().getX();
                var roomSpecs = battle.getModel().getRoom().getSpecs();
                var vehicleMaxRadius = vehicle.getModel().getPreCalc().getMaxRadius();
                var minTargetDistance = vehicleMaxRadius * 5;
                var wallIsRight = roomSpecs.getRightTop().getX() - vehicleX < vehicleMaxRadius;
                var wallIsLeft = vehicleX - roomSpecs.getLeftBottom().getX() < vehicleMaxRadius;
                var vehicleTargetDistance = vehicleX - closestPosition.getX();
                var targetIsRight = closestPosition.getX() > targetData.contact().position().getX();
                state.getGunState().setRotatingDirection(targetIsRight ? MovingDirection.RIGHT : MovingDirection.LEFT);
                if (gunAngle < Math.PI / 3 && targetIsRight) {
                    state.setMovingDirection(MovingDirection.RIGHT);
                } else if (gunAngle > 2 * Math.PI / 3 && !targetIsRight) {
                    state.setMovingDirection(MovingDirection.LEFT);
                } else if (Math.abs(vehicleTargetDistance) < minTargetDistance) {
                    if (vehicleTargetDistance > 0 && !wallIsRight) {
                        state.setMovingDirection(MovingDirection.RIGHT);
                    }
                    if (vehicleTargetDistance < 0 && !wallIsLeft) {
                        state.setMovingDirection(MovingDirection.LEFT);
                    }
                }
            }
        }
        state.getJetState().setActive(state.isTurnedOver());
        if (
                oldTriggerPushed != state.getGunState().isTriggerPushed()
                || oldJetActive != state.getJetState().isActive()
                || isChanged(oldGunRotatingDirection, state.getGunState().getRotatingDirection())
                || isChanged(oldMovingDirection, state.getMovingDirection())
        ) {
            vehicle.getModel().getUpdate().setUpdated();
        }
    }

    private boolean isChanged(Object oldValue, Object newValue) {
        return oldValue != null && newValue == null
                || oldValue == null && newValue != null
                || oldValue != null && !oldValue.equals(newValue);
    }
}
