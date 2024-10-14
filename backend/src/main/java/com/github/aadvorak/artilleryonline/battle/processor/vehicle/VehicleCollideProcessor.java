package com.github.aadvorak.artilleryonline.battle.processor.vehicle;

import com.github.aadvorak.artilleryonline.battle.calculations.VehicleCalculations;
import com.github.aadvorak.artilleryonline.battle.common.Position;
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
            var minDistanceVehicleVehicle = getMinVehicleVehicleDistance(nextPosition, otherVehiclePosition,
                    vehicleRadius, otherVehicleRadius);
            var minDistanceWheelVehicle = wheelRadius + otherVehicleRadius;
            var minDistanceVehicleWheel = vehicleRadius + otherWheelRadius;
            var distanceVehicleVehicle = nextPosition.distanceTo(otherVehiclePosition);
            if (distanceVehicleVehicle < minDistanceVehicleVehicle) {
                return otherVehicleModel;
            }
            var otherLeftWheelPosition = VehicleUtils.getLeftWheelPosition(otherVehicleModel);
            var otherRightWheelPosition = VehicleUtils.getRightWheelPosition(otherVehicleModel);
            var distanceRightWheelLeftWheel = rightWheelPosition.distanceTo(otherLeftWheelPosition);
            if (distanceRightWheelLeftWheel < minDistanceWheelWheel) {
                return otherVehicleModel;
            }
            var distanceLeftWheelRightWheel = leftWheelPosition.distanceTo(otherRightWheelPosition);
            if (distanceLeftWheelRightWheel < minDistanceWheelWheel) {
                return otherVehicleModel;
            }
            var distanceRightWheelVehicle = rightWheelPosition.distanceTo(otherVehiclePosition);
            if (distanceRightWheelVehicle < minDistanceWheelVehicle) {
                return otherVehicleModel;
            }
            var distanceVehicleRightWheel = nextPosition.distanceTo(otherRightWheelPosition);
            if (distanceVehicleRightWheel < minDistanceVehicleWheel) {
                return otherVehicleModel;
            }
            var distanceVehicleLeftWheel = nextPosition.distanceTo(otherLeftWheelPosition);
            if (distanceVehicleLeftWheel < minDistanceVehicleWheel) {
                return otherVehicleModel;
            }
            var distanceLeftWheelVehicle = leftWheelPosition.distanceTo(otherVehiclePosition);
            if (distanceLeftWheelVehicle < minDistanceWheelVehicle) {
                return otherVehicleModel;
            }
            var distanceLeftWheelLeftWheel = leftWheelPosition.distanceTo(otherLeftWheelPosition);
            if (distanceLeftWheelLeftWheel < minDistanceWheelWheel) {
                return otherVehicleModel;
            }
            var distanceRightWheelRightWheel = rightWheelPosition.distanceTo(otherRightWheelPosition);
            if (distanceRightWheelRightWheel < minDistanceWheelWheel) {
                return otherVehicleModel;
            }
        }
        return null;
    }

    private static double getMinVehicleVehicleDistance(Position position, Position otherPosition,
                                                       double radius, double otherRadius) {
        if (position.getY() - otherPosition.getY() > otherRadius) {
            return otherRadius;
        }
        if (otherPosition.getY() - position.getY() > radius) {
            return radius;
        }
        return radius + otherRadius;
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
