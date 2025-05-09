package com.github.aadvorak.artilleryonline.battle.collision.preprocessor.drone;

import com.github.aadvorak.artilleryonline.battle.calculations.BattleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.DroneCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.VehicleCalculations;
import com.github.aadvorak.artilleryonline.battle.collision.preprocessor.CollisionPreprocessor;
import com.github.aadvorak.artilleryonline.battle.common.Collision;
import com.github.aadvorak.artilleryonline.battle.updates.BattleModelRemoved;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;

@Component
public class DroneCollisionPreprocessor implements CollisionPreprocessor {

    @Override
    public boolean process(Collision collision, BattleCalculations battle) {
        var first = collision.getPair().first();
        if (first instanceof DroneCalculations drone) {
            return process(drone, collision, battle);
        }
        return true;
    }

    // todo refactor
    private boolean process(DroneCalculations drone, Collision collision, BattleCalculations battle) {
        if (drone.getModel().getState().isDestroyed()) {
            battle.getModel().getUpdates().removeDrone(drone.getId());
            return false;
        }
        var second = collision.getPair().second();
        if (second instanceof VehicleCalculations vehicle) {
            if (pickDrone(drone, vehicle, battle)) {
                return false;
            }
        }
        var removedDroneIds = Optional.ofNullable(battle.getModel().getUpdates().getRemoved())
                .map(BattleModelRemoved::getDroneIds)
                .orElse(new HashSet<>());
        if (!removedDroneIds.contains(drone.getId())
                && collision.getImpact() > drone.getModel().getSpecs().getMinCollisionDestroyImpact()) {
            drone.getModel().getState().setDestroyed(true);
            battle.getModel().getUpdates().removeDrone(drone.getId());
        }
        drone.getModel().getUpdate().setUpdated();
        return true;
    }

    private boolean pickDrone(DroneCalculations drone, VehicleCalculations vehicle, BattleCalculations battle) {
        if (drone.getModel().getState().getAmmo().values().iterator().next() > 0) {
            return false;
        }
        if (Objects.equals(drone.getModel().getVehicleId(), vehicle.getId())) {
            battle.getModel().getUpdates().removeDrone(drone.getId());
            var inVehicleState = vehicle.getModel().getState().getDroneState();
            inVehicleState.setReadyToLaunch(false);
            inVehicleState.setLaunched(false);
            inVehicleState.setPrepareToLaunchRemainTime(
                    vehicle.getModel().getConfig().getDrone().getPrepareToLaunchTime());
            vehicle.getModel().setUpdated(true);
            return true;
        }
        return false;
    }
}
