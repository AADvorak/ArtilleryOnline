package com.github.aadvorak.artilleryonline.battle.processor.shell.collisions;

import com.github.aadvorak.artilleryonline.battle.calculations.BattleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.ShellCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.VehicleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.WheelCalculations;
import com.github.aadvorak.artilleryonline.battle.common.Collision;
import com.github.aadvorak.artilleryonline.battle.utils.GeometryUtils;
import com.github.aadvorak.artilleryonline.battle.utils.Segment;

public class ShellVehicleCollisionsDetector {

    public static Collision detectFirst(ShellCalculations shell, BattleCalculations battle) {
        for (var vehicle : battle.getVehicles()) {
            var collision = detect(shell, vehicle);
            if (collision != null) {
                return collision;
            }
        }
        return null;
    }

    private static Collision detect(ShellCalculations shell, VehicleCalculations vehicle) {
        var collision = detectWithVehicle(shell, vehicle);
        if (collision != null) {
            return collision;
        }
        collision = detectWithWheel(shell, vehicle.getRightWheel());
        if (collision != null) {
            return collision;
        }
        collision = detectWithWheel(shell, vehicle.getLeftWheel());
        return collision;
    }

    private static Collision detectWithVehicle(ShellCalculations shell, VehicleCalculations vehicle) {
        var position = shell.getPosition();
        var nextPosition = shell.getNext().getPosition();
        var vehiclePosition = vehicle.getPosition();
        var vehicleRadius = vehicle.getModel().getSpecs().getRadius();
        var vehicleAngle = vehicle.getModel().getState().getAngle();
        var shellTrace = new Segment(position, nextPosition);
        var intersectionPoints = GeometryUtils.getSegmentAndCircleIntersectionPoints(shellTrace,
                vehiclePosition, vehicleRadius);
        for (var intersectionPoint : intersectionPoints) {
            var pointAngle = GeometryUtils.getPointAngleInCircle(vehiclePosition, intersectionPoint);
            if (pointAngle > vehicleAngle && pointAngle < vehicleAngle + Math.PI) {
                return Collision.ofShellWithVehicle(shell, vehicle);
            }
        }
        var vehicleBottom = vehicle.getBottomSegment();
        if (GeometryUtils.getSegmentsIntersectionPoint(shellTrace, vehicleBottom) != null) {
            return Collision.ofShellWithVehicle(shell, vehicle);
        }
        return null;
    }

    private static Collision detectWithWheel(ShellCalculations shell, WheelCalculations wheel) {
        var position = shell.getPosition();
        var nextPosition = shell.getNext().getPosition();
        var wheelPosition = wheel.getPosition();
        var wheelRadius = wheel.getModel().getSpecs().getWheelRadius();
        var segment = new Segment(position, nextPosition);
        if (nextPosition.distanceTo(wheelPosition) <= wheelRadius
                || GeometryUtils.isSegmentCrossingCircle(segment, wheelPosition, wheelRadius)) {
            return Collision.ofShellWithVehicle(shell, wheel);
        }
        return null;
    }
}
