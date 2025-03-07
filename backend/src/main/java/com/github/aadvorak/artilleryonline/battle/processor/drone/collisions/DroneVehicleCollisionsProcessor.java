package com.github.aadvorak.artilleryonline.battle.processor.drone.collisions;

import com.github.aadvorak.artilleryonline.battle.calculations.BattleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.DroneCalculations;
import com.github.aadvorak.artilleryonline.battle.common.Collision;
import com.github.aadvorak.artilleryonline.battle.model.VehicleModel;
import com.github.aadvorak.artilleryonline.battle.utils.CollisionUtils;

import java.util.Objects;

public class DroneVehicleCollisionsProcessor {

    public static void process(DroneCalculations drone, BattleCalculations battle) {
        var collision = DroneVehicleCollisionsDetector.detectFirst(drone, battle);
        if (collision != null) {
            if (drone.getModel().getState().isDestroyed()) {
                battle.getModel().getUpdates().removeDrone(drone.getId());
            } else if (!pickDrone(collision, battle)) {
                resolve(collision, battle);
                drone.getModel().getUpdate().setUpdated();
                drone.getCollisions().add(collision);
            }
        }
    }

    private static boolean pickDrone(Collision collision, BattleCalculations battle) {
        var drone = (DroneCalculations) collision.getPair().first();
        if (drone.getModel().getState().getAmmo().values().iterator().next() > 0) {
            return false;
        }
        var vehicle = collision.getPair().second().getVehicleCalculations();
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

    private static void resolve(Collision collision, BattleCalculations battle) {
        CollisionUtils.resolveRigidCollision(collision, battle);
        ((VehicleModel) collision.getPair().second().getModel()).setUpdated(true);
    }
}
