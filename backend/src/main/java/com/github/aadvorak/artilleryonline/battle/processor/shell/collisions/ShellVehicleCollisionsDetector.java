package com.github.aadvorak.artilleryonline.battle.processor.shell.collisions;

import com.github.aadvorak.artilleryonline.battle.calculations.BattleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.ShellCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.VehicleCalculations;
import com.github.aadvorak.artilleryonline.battle.calculations.WheelCalculations;
import com.github.aadvorak.artilleryonline.battle.common.Collision;
import com.github.aadvorak.artilleryonline.battle.common.Interpenetration;
import com.github.aadvorak.artilleryonline.battle.common.Position;
import com.github.aadvorak.artilleryonline.battle.common.lines.Circle;
import com.github.aadvorak.artilleryonline.battle.common.lines.HalfCircle;
import com.github.aadvorak.artilleryonline.battle.utils.GeometryUtils;
import com.github.aadvorak.artilleryonline.battle.common.lines.Segment;

import java.util.Set;
import java.util.stream.Collectors;

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
        var vehicleShape = new HalfCircle(vehiclePosition, vehicleRadius, vehicleAngle);
        var intersectionPoints = GeometryUtils.getSegmentAndCircleIntersectionPoints(shellTrace, vehicleShape.circle())
                .stream().filter(intersectionPoint -> {
                    var pointAngle = GeometryUtils.getPointAngleInCircle(vehiclePosition, intersectionPoint);
                    return pointAngle > vehicleAngle && pointAngle < vehicleAngle + Math.PI;
                }).collect(Collectors.toSet());
        if (!intersectionPoints.isEmpty()) {
            var intersectionPoint = findClosestIntersectionPoint(position, intersectionPoints);
            var interpenetration = Interpenetration.withUncheckedDepth(0.0, intersectionPoint, vehiclePosition);
            return Collision.withVehicle(shell, vehicle, interpenetration);
        }
        var vehicleBottom = vehicleShape.chord();
        if (GeometryUtils.getSegmentsIntersectionPoint(shellTrace, vehicleBottom) != null) {
            var projection = GeometryUtils.getPointToLineProjection(position, vehicleBottom);
            var interpenetration = Interpenetration.withUncheckedDepth(0.0, position, projection);
            return Collision.withVehicle(shell, vehicle, interpenetration);
        }
        return null;
    }

    private static Collision detectWithWheel(ShellCalculations shell, WheelCalculations wheel) {
        var position = shell.getPosition();
        var nextPosition = shell.getNext().getPosition();
        var wheelPosition = wheel.getPosition();
        var wheelRadius = wheel.getModel().getSpecs().getWheelRadius();
        var shellTrace = new Segment(position, nextPosition);
        var wheelShape = new Circle(wheelPosition, wheelRadius);
        var intersectionPoints = GeometryUtils.getSegmentAndCircleIntersectionPoints(shellTrace, wheelShape);
        if (!intersectionPoints.isEmpty()) {
            var intersectionPoint = findClosestIntersectionPoint(position, intersectionPoints);
            var interpenetration = Interpenetration.withUncheckedDepth(0.0, intersectionPoint, wheelPosition);
            return Collision.withVehicle(shell, wheel, interpenetration);
        }
        return null;
    }

    private static Position findClosestIntersectionPoint(Position position, Set<Position> intersectionPoints) {
        var iterator = intersectionPoints.iterator();
        var closest = iterator.next();
        var closestDistance = position.distanceTo(closest);
        while (iterator.hasNext()) {
            var point = iterator.next();
            var distance = position.distanceTo(point);
            if (distance < closestDistance) {
                closest = point;
                closestDistance = distance;
            }
        }
        return closest;
    }
}
