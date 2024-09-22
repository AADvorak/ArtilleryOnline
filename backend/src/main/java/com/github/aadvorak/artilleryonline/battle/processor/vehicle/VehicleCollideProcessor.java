package com.github.aadvorak.artilleryonline.battle.processor.vehicle;

import com.github.aadvorak.artilleryonline.battle.common.Position;
import com.github.aadvorak.artilleryonline.battle.model.BattleModel;
import com.github.aadvorak.artilleryonline.battle.model.VehicleModel;
import com.github.aadvorak.artilleryonline.battle.utils.VehicleUtils;

import java.util.stream.Collectors;

public class VehicleCollideProcessor {

    public static boolean processCollide(VehicleModel vehicleModel, BattleModel battleModel, Position nextPosition) {
        var vehicleCollide = vehicleCollide(vehicleModel, battleModel, nextPosition);
        if (vehicleCollide != null) {
            doCollide(vehicleModel, vehicleCollide);
            battleModel.setUpdated(true);
            return true;
        }
        return false;
    }

    private static VehicleModel vehicleCollide(VehicleModel vehicleModel, BattleModel battleModel, Position nextPosition) {
        var otherVehicleModels = battleModel.getVehicles().values().stream()
                .filter(value -> value.getId() != vehicleModel.getId())
                .collect(Collectors.toSet());
        var velocity = vehicleModel.getState().getVelocity();
        var wheelRadius = vehicleModel.getSpecs().getWheelRadius();
        var vehicleRadius = vehicleModel.getSpecs().getRadius();
        var rightWheelPosition = VehicleUtils.getRightWheelPosition(vehicleModel, nextPosition);
        var leftWheelPosition = VehicleUtils.getLeftWheelPosition(vehicleModel, nextPosition);
        for (var otherVehicleModel : otherVehicleModels) {
            var otherVehiclePosition = otherVehicleModel.getState().getPosition();
            var otherWheelRadius = otherVehicleModel.getSpecs().getWheelRadius();
            var otherVehicleRadius = otherVehicleModel.getSpecs().getRadius();
            var minDistanceWheelWheel = wheelRadius + otherWheelRadius;
            var minDistanceVehicleVehicle = vehicleRadius + otherVehicleRadius;
            var minDistanceWheelVehicle = wheelRadius + otherVehicleRadius;
            var minDistanceVehicleWheel = vehicleRadius + otherWheelRadius;
            var distanceVehicleVehicle = nextPosition.distanceTo(otherVehiclePosition);
            if (distanceVehicleVehicle < minDistanceVehicleVehicle) {
                return otherVehicleModel;
            }
            if (velocity > 0) {
                var otherLeftWheelPosition = VehicleUtils.getLeftWheelPosition(otherVehicleModel);
                var distanceWheelWheel = rightWheelPosition.distanceTo(otherLeftWheelPosition);
                if (distanceWheelWheel < minDistanceWheelWheel) {
                    return otherVehicleModel;
                }
                var distanceWheelVehicle = rightWheelPosition.distanceTo(otherVehiclePosition);
                if (distanceWheelVehicle < minDistanceWheelVehicle) {
                    return otherVehicleModel;
                }
                var distanceVehicleWheel = nextPosition.distanceTo(otherLeftWheelPosition);
                if (distanceVehicleWheel < minDistanceVehicleWheel) {
                    return otherVehicleModel;
                }
            }
            if (velocity < 0) {
                var otherRightWheelPosition = VehicleUtils.getRightWheelPosition(otherVehicleModel);
                var distanceWheelWheel = leftWheelPosition.distanceTo(otherRightWheelPosition);
                if (distanceWheelWheel < minDistanceWheelWheel) {
                    return otherVehicleModel;
                }
                var distanceWheelVehicle = leftWheelPosition.distanceTo(otherVehiclePosition);
                if (distanceWheelVehicle < minDistanceWheelVehicle) {
                    return otherVehicleModel;
                }
                var distanceVehicleWheel = nextPosition.distanceTo(otherRightWheelPosition);
                if (distanceVehicleWheel < minDistanceVehicleWheel) {
                    return otherVehicleModel;
                }
            }
        }
        return null;
    }

    private static void doCollide(VehicleModel vehicle, VehicleModel otherVehicle) {
        var vehicleVelocity = vehicle.getState().getVelocity();
        var otherVehicleVelocity = otherVehicle.getState().getVelocity();
        if (vehicleVelocity * otherVehicleVelocity > 0) {
            vehicle.getState().setVelocity(otherVehicleVelocity);
            otherVehicle.getState().setVelocity(vehicleVelocity);
        } else {
            vehicle.getState().setVelocity(otherVehicleVelocity / 2);
            otherVehicle.getState().setVelocity(vehicleVelocity / 2);
        }
        otherVehicle.setCollided(true);
    }
}
