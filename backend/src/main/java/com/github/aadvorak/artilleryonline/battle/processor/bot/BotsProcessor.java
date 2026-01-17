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
        var gunAngle = state.getPosition().getAngle() + state.getGunState().getAngle();
        var targetData = targetDataCalculator.calculate(vehicle, battle);
        state.getGunState().setTriggerPushed(targetData != null && targetData.armor() != null); // todo check penetration or use HE
        if (targetData == null) {
            state.getGunState().setRotatingDirection(gunAngle > Math.PI / 2 ? MovingDirection.RIGHT : MovingDirection.LEFT);
        } else if (targetData.armor() == null) {
            var otherVehiclePositions = battle.getVehicles().stream()
                    .filter(item -> !vehicle.getId().equals(item.getId()))
                    .map(VehicleCalculations::getPosition)
                    .collect(Collectors.toSet());
            var closestPosition = GeometryUtils.findClosestPosition(targetData.contact().position(), otherVehiclePositions);
            if (closestPosition != null) {
                var targetIsRight = closestPosition.getX() > targetData.contact().position().getX();
                state.getGunState().setRotatingDirection(targetIsRight ? MovingDirection.RIGHT : MovingDirection.LEFT);
            }
        }
        state.getJetState().setActive(state.isTurnedOver());
        if (
                oldTriggerPushed != state.getGunState().isTriggerPushed()
                || oldJetActive != state.getJetState().isActive()
                || isChanged(oldGunRotatingDirection, state.getGunState().getRotatingDirection())
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
