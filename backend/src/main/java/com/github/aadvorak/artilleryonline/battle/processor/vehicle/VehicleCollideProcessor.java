package com.github.aadvorak.artilleryonline.battle.processor.vehicle;

import com.github.aadvorak.artilleryonline.battle.calculations.VehicleCalculations;
import com.github.aadvorak.artilleryonline.battle.model.BattleModel;
import com.github.aadvorak.artilleryonline.battle.model.VehicleModel;
import com.github.aadvorak.artilleryonline.battle.utils.VehicleUtils;

import java.util.stream.Collectors;

public class VehicleCollideProcessor {

    public static boolean processCollide(VehicleCalculations calculations, VehicleModel vehicleModel,
                                         BattleModel battleModel) {
        var vehicleCollide = vehicleCollide(calculations, vehicleModel, battleModel);
        if (vehicleCollide != null) {
            doCollide(vehicleModel, vehicleCollide);
            vehicleModel.setUpdated(true);
            return true;
        }
        return false;
    }

    private static VehicleModel vehicleCollide(VehicleCalculations calculations, VehicleModel vehicleModel,
                                               BattleModel battleModel) {
        var otherVehicleModels = battleModel.getVehicles().values().stream()
                .filter(value -> value.getId() != vehicleModel.getId())
                .collect(Collectors.toSet());
        var velocity = vehicleModel.getState().getVelocity().getX();
        var wheelRadius = vehicleModel.getSpecs().getWheelRadius();
        var vehicleRadius = vehicleModel.getSpecs().getRadius();
        var nextPosition = calculations.getNextPosition();
        var nextAngle = calculations.getNextAngle();
        var rightWheelPosition = VehicleUtils.getRightWheelPosition(vehicleModel, nextPosition, nextAngle);
        var leftWheelPosition = VehicleUtils.getLeftWheelPosition(vehicleModel, nextPosition, nextAngle);
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
        var vehicleVelocityX = vehicleVelocity.getX();
        if (vehicleVelocity.getX() * otherVehicleVelocity.getX() > 0) {
            vehicleVelocity.setX(otherVehicleVelocity.getX());
            otherVehicleVelocity.setX(vehicleVelocityX);
        } else {
            vehicleVelocity.setX(otherVehicleVelocity.getX() / 2);
            otherVehicleVelocity.setX(vehicleVelocityX / 2);
        }
        var vehicleVelocityY = vehicleVelocity.getY();
        if (vehicleVelocity.getY() * otherVehicleVelocity.getY() > 0) {
            vehicleVelocity.setY(otherVehicleVelocity.getY());
            otherVehicleVelocity.setY(vehicleVelocityY);
        } else {
            vehicleVelocity.setY(otherVehicleVelocity.getY() / 2);
            otherVehicleVelocity.setY(vehicleVelocityY / 2);
        }
        otherVehicle.setCollided(true);
        otherVehicle.setUpdated(true);
    }
}
